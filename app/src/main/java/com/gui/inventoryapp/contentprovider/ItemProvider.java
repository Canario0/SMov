package com.gui.inventoryapp.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.gui.inventoryapp.constant.ItemConstants;
import com.gui.inventoryapp.database.DbHelper;

public class ItemProvider extends ContentProvider {
    private static final String TAG = ItemProvider.class.getSimpleName();
    private DbHelper dbHelper;
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(ItemConstants.AUTHORITY, ItemConstants.TABLE, ItemConstants.DATA_ITEMS);
        sURIMatcher.addURI(ItemConstants.AUTHORITY, ItemConstants.TABLE + "/#", ItemConstants.DATA_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        Log.d(TAG, "onCreated");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String where = selection;
        if ((sURIMatcher.match(uri)) == ItemConstants.DATA_ITEM) {
            where = ItemConstants.ITEM.BARCODE
                    + "=" + ContentUris.parseId(uri)
                    + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(ItemConstants.TABLE, projection, where, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(TAG, "query, espero que esto funque");
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case ItemConstants.DATA_ITEMS:
                Log.d(TAG, "gotType: vnd.android.cursor.dir/vnd.com.gui.inventoryapp.contentprovider.ItemProvider");
                return "vnd.android.cursor.dir/vnd.com.gui.inventoryapp.contentprovider.ItemProvider";
            case ItemConstants.DATA_ITEM:
                Log.d(TAG, "gotType: vnd.android.cursor.item/vnd.com.gui.inventoryapp.contentprovider.ItemProvider");
                return "vnd.android.cursor.item/vnd.com.gui.inventoryapp.contentprovider.ItemProvider";
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        /**
         * Add a new student record
         */
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long rowID = db.insert(ItemConstants.TABLE	, "", values);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(ItemConstants.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }else {
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where = selection;
        if ((sURIMatcher.match(uri)) == ItemConstants.DATA_ITEM) {
            where = ItemConstants.ITEM.BARCODE
                    + "=" + ContentUris.parseId(uri)
                    + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int rows = db.delete(ItemConstants.TABLE, where, selectionArgs);
        Log.d(TAG, "delete, espero que esto funque");
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where = selection;
        if ((sURIMatcher.match(uri)) == ItemConstants.DATA_ITEM) {
            where = ItemConstants.ITEM.BARCODE
                    + "=" + ContentUris.parseId(uri)
                    + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int rows = db.update(ItemConstants.TABLE, values, where, selectionArgs);
        Log.d(TAG, "update, espero que esto funque");
        return rows;
    }
}
