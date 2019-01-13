package com.gui.inventoryapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.database.DatabaseConstants;
import com.gui.inventoryapp.interfaces.ListCommon;
import com.gui.inventoryapp.utils.BarcodeScanner;

import org.w3c.dom.Text;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.gui.inventoryapp.utils.GeneralConstants.CAMERA_REQUEST;
import static com.gui.inventoryapp.utils.GeneralConstants.MY_CAMERA_PERMISSION_CODE;


public class MemberList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, ListCommon {

    private static final String TAG = MemberList.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {DatabaseConstants.Member.ALIAS,
            DatabaseConstants.Member.NAME};
    private static final int[] TO = {R.id.member_alias, R.id.member_name};
    private static final int LOADER_ID = 43;
    private BarcodeScanner barscan;
    private long current_open = 0;

    private ImageView image_barcode;
    private TextView item_barcode;

    private static int STATUS_ITEM_DAMAGED = 0;
    private static int STATUS_ITEM_AVAILABLE = 1;
    private static int STATUS_ITEM_ONLOAN = 2;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("Sin datos, hable con el servicio tecnico...");
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.user, null, FROM, TO, 0);
        mAdapter.setViewBinder(new TimelineViewBinder());
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        barscan = new BarcodeScanner(this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");

        if (bundle == null)
            return new CursorLoader(getActivity(), Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER), null, null, null, DatabaseConstants.DEFAULT_SORT_M);
        else
            return new CursorLoader(getActivity(), Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER), null, bundle.getString("selection"), null, DatabaseConstants.DEFAULT_SORT_M);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished with cursor: " + cursor.getCount());
        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void setDialogView(View aux, Cursor x) {

        //((TextView) aux.findViewById(R.id.item_id)).setText(x.getString(x.getColumnIndex(DatabaseConstants.Item.ID)));

        ((TextView) aux.findViewById(R.id.member_dialog_title)).setText(x.getString(x.getColumnIndex(DatabaseConstants.Member.ALIAS)));
        ((TextView) aux.findViewById(R.id.member_dialog_name)).setText(x.getString(x.getColumnIndex(DatabaseConstants.Member.NAME)));
        ((TextView) aux.findViewById(R.id.member_dialog_apellidos)).setText(x.getString(x.getColumnIndex(DatabaseConstants.Member.LASTNAME)));
        ((TextView) aux.findViewById(R.id.member_dialog_email)).setText(x.getString(x.getColumnIndex(DatabaseConstants.Member.EMAIL)));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor x = (Cursor) this.getListAdapter().getItem(position);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final AlertDialog alertDialog;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        current_open = x.getInt(x.getColumnIndex(DatabaseConstants.Item.ID));
        final View aux = inflater.inflate(R.layout.member_dialog, null);
        setDialogView(aux, x);
//         set dialog message
        alertDialogBuilder.setView(aux)
                .setCancelable(true)
                .setNegativeButton(R.string.salir_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        current_open = 0;
                        dialog.cancel();
                    }
                });

        //Listeners
        ((Button) aux.findViewById(R.id.member_dialog_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable barcode = ((EditText) aux.findViewById(R.id.member_dialog_barcode)).getText();
                Date input;
                Date today;
                Cursor cursor_item;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                //Comprobar barcode
                if (!barcode.toString().equals("")) {

                    String selection = String.format("%s LIKE '%s'", DatabaseConstants.Item.BARCODE, barcode.toString());
                    cursor_item = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_ITEM),
                            null,
                            selection,
                            null,
                            null);

                    if (cursor_item.getCount() > 0) {
                        cursor_item.moveToFirst();
                        //Testing if is on loan
                        selection = String.format(DatabaseConstants.ACTIVE_LOAN_SELECTION, cursor_item.getInt(cursor_item.getColumnIndex(DatabaseConstants.Item.ID)));

                        Cursor cursor_loan = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_LOAN),
                                null,
                                selection,
                                null,
                                null);

                        //if is on loan
                        if (cursor_loan.getCount() > 0) {
                            Toast.makeText(getContext(), "EL Item con Barcode = " + barcode.toString() + " se encuentra prestado", Toast.LENGTH_LONG).show();
                            barcode.clear();
                            return;
                        }

                        cursor_loan.close();
                    } else {
                        Toast.makeText(getContext(), "EL Item con Barcode = " + barcode.toString() + " no existe", Toast.LENGTH_LONG).show();
                        barcode.clear();
                        return;
                    }

                } else {
                    Toast.makeText(getContext(), "Rellene el campo Barcode", Toast.LENGTH_LONG).show();
                    barcode.clear();
                    return;
                }
                Log.d(TAG, barcode.toString());

                //Comprobar fecha

                Editable date = ((EditText) aux.findViewById(R.id.member_dialog_date)).getText();
                if (!date.toString().equals("")) {

                    ParsePosition error = new ParsePosition(0);

                    Calendar calendar = Calendar.getInstance();
                    input = dateFormat.parse(date.toString(), error);
                    today = calendar.getTime();
                    if (error.getErrorIndex() != -1) {
                        Toast.makeText(getContext(), "EL formato de la fecha no es correcto", Toast.LENGTH_LONG).show();
                        date.clear();
                        return;
                    } else if (!input.after(today)) {
                        Toast.makeText(getContext(), "La fecha introducida es incorrecta", Toast.LENGTH_LONG).show();
                        date.clear();
                        return;
                    }
                } else {
                    Toast.makeText(getContext(), "Rellene el campo Date", Toast.LENGTH_LONG).show();
                    date.clear();
                    return;
                }
                String title = (String) ((TextView) aux.findViewById(R.id.member_dialog_title)).getText();

                String selection = String.format("%s LIKE '%s'", DatabaseConstants.Member.ALIAS, title);
                Cursor cursor_member = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER),
                        null,
                        selection,
                        null,
                        null);
                cursor_member.moveToFirst();

                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.Loan.START_OF_LOAN, dateFormat.format(today));
                values.put(DatabaseConstants.Loan.END_OF_LOAN, dateFormat.format(today));
                values.put(DatabaseConstants.Loan.MEMBER, cursor_member.getInt(cursor_member.getColumnIndex(DatabaseConstants.Member.ID)));
                values.put(DatabaseConstants.Loan.ITEM, cursor_item.getInt(cursor_item.getColumnIndex(DatabaseConstants.Item.ID)));
                Uri out = getActivity().getContentResolver().insert(
                        Uri.parse(DatabaseConstants.CONTENT_URI_LOAN),   // the user dictionary content URI
                        values                       // the columns to update
                );
                cursor_member.close();
                cursor_item.close();
                Toast.makeText(getContext(), "Prestamo Creado", Toast.LENGTH_LONG).show();
                barcode.clear();
                date.clear();
            }
        });

        image_barcode = ((ImageView)aux.findViewById(R.id.scan_barcode));
        item_barcode = ((TextView)aux.findViewById(R.id.member_dialog_barcode));
        //Barcode
        image_barcode.setOnClickListener(barscan);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();

    }

    @Override
    public void update(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("selection", DatabaseConstants.Member.ALIAS + " LIKE '" + id + "%'");
        getLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public void reset() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            if (view.getId() != R.id.member_name)
                return false;
            //Load Name
            ((TextView) view).setText(String.format("%s %s", cursor.getString(2), cursor.getString(3)));

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case MY_CAMERA_PERMISSION_CODE:
                if(image_barcode != null)
                    image_barcode.performClick();
                break;

            default:
                throw new UnsupportedOperationException();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //No se realiza ninguna acción
        if(resultCode != Activity.RESULT_OK)
            return;

        switch(requestCode) {
            case CAMERA_REQUEST:
                String str = BarcodeScanner.getBarCode(barscan.getPath(),getContext());
                if(str != null && item_barcode != null)
                    item_barcode.setText(str); //Colocamos el texto en el campo
                else
                    Toast.makeText(getContext(), "No se reconoce ningún código de barras code_39", Toast.LENGTH_LONG).show();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
