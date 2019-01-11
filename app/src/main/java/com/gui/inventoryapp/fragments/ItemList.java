package com.gui.inventoryapp.fragments;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.constant.ItemConstants;
import com.gui.inventoryapp.contentprovider.ItemProvider;

import java.util.zip.Inflater;


public class ItemList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String TAG = ItemList.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {ItemConstants.ITEM.BARCODE,
            ItemConstants.ITEM.CONDITION};
    private static final int[] TO = {R.id.item_barcode, R.id.item_condition};
    private static final int LOADER_ID = 42;
    String current_open = "";

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
                ((Spinner) aux.findViewById(R.id.item_state)).setSelection(x.getInt(x.getColumnIndex(ItemConstants.ITEM.CONDITION)) + 1);
                break;
            case 0:
                ((Spinner) aux.findViewById(R.id.item_state)).setSelection(x.getInt(x.getColumnIndex(ItemConstants.ITEM.CONDITION)) + 1);
                break;
            case 1:
                ((Spinner) aux.findViewById(R.id.item_state)).setSelection(x.getInt(x.getColumnIndex(ItemConstants.ITEM.CONDITION)) + 1);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final AlertDialog alertDialog;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        current_open = x.getString(x.getColumnIndex(ItemConstants.ITEM.BARCODE));
        View aux = inflater.inflate(R.layout.item_dialog, null);
        setDialogView(aux, x);
//         set dialog messag
        alertDialogBuilder.setView(aux)
                .setCancelable(false)
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        current_open = "";
                        dialog.cancel();
                    }
                });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // Listeners

        ((Spinner) aux.findViewById(R.id.item_state)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    Toast.makeText(getActivity(), "No está implementado todavía", Toast.LENGTH_LONG).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.putNull(ItemConstants.ITEM.GIVEN_TO);
                    values.putNull(ItemConstants.ITEM.CHECKOUT_EXPIRE_DATE);
                    values.put(ItemConstants.ITEM.CONDITION, position - 1);
                    int mRowsUpdated = getActivity().getContentResolver().update(
                            ItemConstants.CONTENT_URI,   // the user dictionary content URI
                            values,                       // the columns to update
                            ItemConstants.ITEM.BARCODE + " LIKE '" + current_open + "'",                    // the column to select on
                            null                    // the value to compare to
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
                        ItemConstants.CONTENT_URI,   // the user dictionary content URI
                        ItemConstants.ITEM.BARCODE + " LIKE '" + current_open + "'",                    // the column to select on
                        null                    // the value to compare to
                );

                if (mRowsUpdated != 0) {
                    reset();
                    current_open = "";
                    alertDialog.cancel();
                }
            }
        });

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
            if (view.getId() == R.id.item_state) {
                switch (cursor.getInt(columnIndex)) {
                    case -1:
                        ((Spinner) view.findViewById(R.id.item_state)).setSelection(cursor.getInt(columnIndex) + 1);
                        break;
                    case 0:
                        ((Spinner) view.findViewById(R.id.item_state)).setSelection(cursor.getInt(columnIndex) + 1);
                        break;
                    case 1:
                        ((Spinner) view.findViewById(R.id.item_state)).setSelection(cursor.getInt(columnIndex) + 1);
                        break;

                }
                return true;
            } else if (view.getId() == R.id.item_condition) {
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


            return false;
        }
    }
}
