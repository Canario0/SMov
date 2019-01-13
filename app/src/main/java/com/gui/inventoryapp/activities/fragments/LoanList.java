package com.gui.inventoryapp.activities.fragments;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.database.DatabaseConstants;
import com.gui.inventoryapp.utils.interfaces.ListCommon;


public class LoanList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = LoanList.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {DatabaseConstants.Loan.ITEM,
            DatabaseConstants.Loan.MEMBER, DatabaseConstants.Loan.END_OF_LOAN};
    private static final int[] TO = {R.id.loan_barcode, R.id.loan_alias, R.id.loan_end};
    private static final int LOADER_ID = 45;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("No hay préstamos nuevos...");
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.loan, null, FROM, TO, 0);
        mAdapter.setViewBinder(new TimelineViewBinder());
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");

            return new CursorLoader(getActivity(), Uri.parse(DatabaseConstants.CONTENT_URI_LOAN), null, DatabaseConstants.Loan.RETURNED +"="+ 0, null, DatabaseConstants.DEFAULT_SORT_L);
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

            if (view.getId() == R.id.loan_barcode){
                String selection = String.format("%s = %d", DatabaseConstants.Item.ID, cursor.getInt(columnIndex));
                Cursor cursor_item = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_ITEM),
                        null,
                        selection,
                        null,
                        null);
                cursor_item.moveToFirst();
                Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor_item)+ " " + cursor.getInt(columnIndex));
                ((TextView) view).setText(cursor_item.getString(cursor_item.getColumnIndex(DatabaseConstants.Item.BARCODE)));
                cursor_item.close();
                return true;
            }else if(view.getId() == R.id.loan_alias){
                String selection = String.format("%s = %d", DatabaseConstants.Member.ID, cursor.getInt(columnIndex));
                Cursor cursor_member = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER),
                        null,
                        selection,
                        null,
                        null);
                cursor_member.moveToFirst();
                ((TextView) view).setText(cursor_member.getString(cursor_member.getColumnIndex(DatabaseConstants.Member.ALIAS)));
                cursor_member.close();
                return true;

            }

            return false;
        }
    }
}
