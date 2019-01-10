package com.gui.inventoryapp.fragments;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.constant.ItemConstants;

import java.util.zip.Inflater;


public class ItemList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String TAG = ItemList.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {ItemConstants.ITEM.BARCODE,
            ItemConstants.ITEM.CONDITION};
    private static final int[] TO = {R.id.item_barcode, R.id.item_condition};
    private static final int LOADER_ID = 42;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("Sin datos, registrar un nuvo elemento...");
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
            return new CursorLoader(getActivity(), ItemConstants.CONTENT_URI, null, null, null, ItemConstants.DEFAULT_SORT);
        else
            return new CursorLoader(getActivity(), ItemConstants.CONTENT_URI, null, bundle.getString("selection"), null, ItemConstants.DEFAULT_SORT);
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
        ((TextView) aux.findViewById(R.id.item_id)).setText(x.getString(x.getColumnIndex(ItemConstants.ITEM.ID)));
        ((TextView) aux.findViewById(R.id.dialog_title)).setText(x.getString(x.getColumnIndex(ItemConstants.ITEM.BARCODE)));
        switch (x.getInt(x.getColumnIndex(ItemConstants.ITEM.CONDITION))) {
            case -1:
                ((Spinner) aux.findViewById(R.id.item_state)).setSelection(x.getInt(x.getColumnIndex(ItemConstants.ITEM.CONDITION))+ 1);
                break;
            case 0:
                ((Spinner) aux.findViewById(R.id.item_state)).setSelection(x.getInt(x.getColumnIndex(ItemConstants.ITEM.CONDITION))+ 1);
                break;
            case 1:
                ((Spinner) aux.findViewById(R.id.item_state)).setSelection(x.getInt(x.getColumnIndex(ItemConstants.ITEM.CONDITION))+ 1);
                break;

        }

        ((TextView) aux.findViewById(R.id.added_date)).setText(x.getString(x.getColumnIndex(ItemConstants.ITEM.ENTRY_DATE)));
        ((TextView) aux.findViewById(R.id.book_end_date)).setText(x.getString(x.getColumnIndex(ItemConstants.ITEM.CHECKOUT_EXPIRE_DATE)));
        ((TextView) aux.findViewById(R.id.rent_user)).setText(x.getString(x.getColumnIndex(ItemConstants.ITEM.GIVEN_TO)));
        ((TextView) aux.findViewById(R.id.owner_name)).setText(x.getString(x.getColumnIndex(ItemConstants.ITEM.OWNER)));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor x = (Cursor) this.getListAdapter().getItem(position);
        TextView v = view.findViewById(R.id.item_barcode);
//        Toast.makeText(getActivity(), "YEY MA BOY soy el item: " + position + " y contengo " + v.getText(), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // set title
        View aux = inflater.inflate(R.layout.item_dialog, null);
        setDialogView(aux, x);
        // set dialog message
        alertDialogBuilder.setView(aux)
                .setCancelable(true)
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void update(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("selection", ItemConstants.ITEM.BARCODE + " LIKE '" + id + "%'");
        getLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    public void reset() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() != R.id.item_condition)
                return false;
            switch (cursor.getInt(columnIndex)) {
                case -1:
                    ((TextView) view).setTextColor(Color.parseColor("#DC3545"));
                    ((TextView) view).setText("averiado");
                    break;
                case 0:
                    ((TextView) view).setTextColor(Color.parseColor("#28A745"));
                    ((TextView) view).setText("disponible");
                    break;
                case 1:
                    ((TextView) view).setTextColor(Color.parseColor("#FFC107"));
                    ((TextView) view).setText("prestado");
                    break;
            }

            return true;
        }
    }
}
