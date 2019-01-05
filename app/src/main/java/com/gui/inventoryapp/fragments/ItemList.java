package com.gui.inventoryapp.fragments;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.database.DatabaseConstants;


public class ItemList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ItemList.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {DatabaseConstants.Item.BARCODE,
            DatabaseConstants.Item.DAMAGED};
    private static final int[] TO = {R.id.item_barcode, R.id.item_condition};
    private static final int LOADER_ID = 42;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("Sin datos, registrar un nuvo elemento...");
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item, null, FROM, TO, 0);
        mAdapter.setViewBinder(new TimelineViewBinder());
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(getActivity(), DatabaseConstants.CONTENT_URI, null, null, null, DatabaseConstants.DEFAULT_SORT);
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

    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() != R.id.item_condition)
                return false;
            switch (cursor.getInt(columnIndex)){
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
