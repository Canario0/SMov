package com.gui.inventoryapp.fragments;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.database.DatabaseConstants;
import com.gui.inventoryapp.interfaces.ListCommon;


public class ItemList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, ListCommon {

    private static final String TAG = ItemList.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {DatabaseConstants.Item.BARCODE,
            DatabaseConstants.Item.DAMAGED};
    private static final int[] TO = {R.id.item_barcode, R.id.item_condition};
    private static final int LOADER_ID = 42;
    private long current_open = 0;

    private static int STATUS_ITEM_DAMAGED = 0;
    private static int STATUS_ITEM_AVAILABLE = 1;
    private static int STATUS_ITEM_ONLOAN = 2;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("Sin datos, registrar un nuevo elemento...");
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item, null, FROM, TO, 0);
        mAdapter.setViewBinder(new TimelineViewBinder());
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");

        if (bundle == null)
            return new CursorLoader(getActivity(), Uri.parse(DatabaseConstants.CONTENT_URI_ITEM), null, null, null, DatabaseConstants.DEFAULT_SORT_I);
        else
            return new CursorLoader(getActivity(), Uri.parse(DatabaseConstants.CONTENT_URI_ITEM), null, bundle.getString("selection"), null, DatabaseConstants.DEFAULT_SORT_I);
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


        ((TextView) aux.findViewById(R.id.item_dialog_title)).setText(x.getString(x.getColumnIndex(DatabaseConstants.Item.BARCODE)));

        //Si está averiado
        if (x.getInt(x.getColumnIndex(DatabaseConstants.Item.DAMAGED)) == 1) {
            ((Spinner) aux.findViewById(R.id.item_state)).setSelection(STATUS_ITEM_DAMAGED);
        } else {

            // Se seleccionan los préstamos sin devolver
            String selection = String.format(DatabaseConstants.ACTIVE_LOAN_SELECTION,
                    x.getInt(x.getColumnIndex(DatabaseConstants.Item.ID)));

            Cursor cursor = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_LOAN),
                    null,
                    selection,
                    null,
                    null);
            Log.d("!--.", String.format("COUNT: %d",cursor.getCount()));

            //Si está prestado
            if (cursor.getCount() > 0) {
                ((Spinner) aux.findViewById(R.id.item_state)).setSelection(STATUS_ITEM_ONLOAN);
                cursor.moveToNext();
                ((TextView) aux.findViewById(R.id.book_end_date)).setText(cursor.getString(cursor.getColumnIndex(DatabaseConstants.Loan.END_OF_LOAN)));
                ((TextView) aux.findViewById(R.id.rent_user)).setText(cursor.getString(cursor.getColumnIndex(DatabaseConstants.Loan.MEMBER)));
            } else {
                ((Spinner) aux.findViewById(R.id.item_state)).setSelection(STATUS_ITEM_AVAILABLE);
            }
            cursor.close();
        }

        ((TextView) aux.findViewById(R.id.added_date)).setText(x.getString(x.getColumnIndex(DatabaseConstants.Item.ENTRY_DATE)));

       Cursor cursor = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER + "/" + x.getString(x.getColumnIndex(DatabaseConstants.Item.OWNER))),
                null,
                null,
                null,
                null);

       if(cursor.getCount() > 0) {
           cursor.moveToNext();
           ((TextView) aux.findViewById(R.id.owner_name)).setText(cursor.getString(cursor.getColumnIndex(DatabaseConstants.Member.ALIAS)));
       }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor x = (Cursor) this.getListAdapter().getItem(position);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final AlertDialog alertDialog;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        current_open = x.getInt(x.getColumnIndex(DatabaseConstants.Item.ID));
        View aux = inflater.inflate(R.layout.item_dialog, null);
        setDialogView(aux, x);
//         set dialog messag
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

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // Listeners
        ((Spinner) aux.findViewById(R.id.item_state)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public boolean selected = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(selected == false) {selected = true; return;} //First
                if (position == STATUS_ITEM_ONLOAN) {
                    Toast.makeText(getActivity(), "No está implementado todavía. Nuevos préstamos en: Miembros->click en miembro", Toast.LENGTH_LONG).show();
                } else {
                    ContentValues values = new ContentValues();
                    //values.putNull(DatabaseConstants.Item.GIVEN_TO);
                    //values.putNull(ItemConstants.ITEM.CHECKOUT_EXPIRE_DATE);
                    values.put(DatabaseConstants.Item.DAMAGED, Math.abs(position - 1));

                    int mRowsUpdated = getActivity().getContentResolver().update(
                            Uri.parse(DatabaseConstants.CONTENT_URI_ITEM + "/" + current_open),   // the user dictionary content URI
                            values,                       // the columns to update
                            null,                    // the column to select on
                            null                    // the value to compare to
                    );

                    values.clear();
                    values.put(DatabaseConstants.Loan.RETURNED,1);
                    String selection = String.format(DatabaseConstants.ACTIVE_LOAN_SELECTION,current_open);
                    mRowsUpdated += getActivity().getContentResolver().update(
                            Uri.parse(DatabaseConstants.CONTENT_URI_LOAN),
                            values,
                            selection,
                            null
                    );

                    if (mRowsUpdated != 0) {
                        reset();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        ((ImageView) aux.findViewById(R.id.delete_item)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mRowsUpdated = getActivity().getContentResolver().delete(
                        Uri.parse(DatabaseConstants.CONTENT_URI_ITEM + "/" + current_open),   // the user dictionary content URI
                        null,                    // the column to select on
                        null                    // the value to compare to
                );

                if (mRowsUpdated != 0) {
                    reset();
                    current_open = 0;
                    alertDialog.cancel();
                }
            }
        });

        // show it
        alertDialog.show();

    }

    @Override
    public void update(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("selection", DatabaseConstants.Item.BARCODE + " LIKE '" + id + "%'");
        getLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public void reset() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            if (view.getId() != R.id.item_condition)
                return false;

            //Damaged
            if (cursor.getInt(columnIndex) == 1) {
                ((TextView) view).setTextColor(ContextCompat.getColor(getContext(), R.color.status_item_damaged));;
                ((TextView) view).setText(R.string.status_item_damaged);
                return true;
            }

            //Available
            ((TextView) view).setTextColor(ContextCompat.getColor(getContext(), R.color.status_item_available));
            ((TextView) view).setText(R.string.status_item_available);

            //Testing if is on loan
            String selection = String.format(DatabaseConstants.ACTIVE_LOAN_SELECTION,
                    cursor.getInt(cursor.getColumnIndex(DatabaseConstants.Item.ID)));

            Cursor cursor_loan = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_LOAN),
                    null,
                    selection,
                    null,
                    null);

            //if is on loan
            if (cursor_loan.getCount() > 0) {
                ((TextView) view).setTextColor(ContextCompat.getColor(getContext(), R.color.status_item_onLoan));
                ((TextView) view).setText(R.string.status_item_onLoan);
            }

            cursor_loan.close();

            return true;
        }
    }
}
