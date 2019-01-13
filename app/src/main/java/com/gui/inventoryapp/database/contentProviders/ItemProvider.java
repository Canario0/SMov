package com.gui.inventoryapp.database.contentProviders;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.gui.inventoryapp.database.DatabaseConstants;
import com.gui.inventoryapp.database.DbHelper;

public class ItemProvider extends ContentProvider {
    private static final String TAG = MemberProvider.class.getSimpleName();
    private DbHelper dbHelper;
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(DatabaseConstants.AUTHORITY_ITEM, DatabaseConstants.TABLE_ITEM, DatabaseConstants.CASE_ITEMS);
        sURIMatcher.addURI(DatabaseConstants.AUTHORITY_ITEM, DatabaseConstants.TABLE_ITEM + "/*", DatabaseConstants.CASE_ITEM);
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
        String where;
        switch (sURIMatcher.match(uri)) {
            case DatabaseConstants.CASE_ITEMS:
                where = selection;
                break;
            case DatabaseConstants.CASE_ITEM:
                long id = ContentUris.parseId(uri);
                where = DatabaseConstants.Item.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder)) ? DatabaseConstants.DEFAULT_SORT_I : sortOrder;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseConstants.TABLE_ITEM, projection, where, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        //Log.d(TAG,  cursor.getCount() + " registros recuperados");
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case DatabaseConstants.CASE_ITEMS:
                Log.d(TAG, "gotType: vnd.android.cursor.dir/vnd.com.gui.inventoryapp.database.contentProviders.ItemProvider");
                return "vnd.android.cursor.dir/vnd.com.gui.inventoryapp.database.contentProviders.ItemProvider";
            case DatabaseConstants.CASE_ITEM:
                Log.d(TAG, "gotType: vnd.android.cursor.item/vnd.com.gui.inventoryapp.database.contentProviders.ItemProvider");
                return "vnd.android.cursor.item/vnd.com.gui.inventoryapp.database.contentProviders.ItemProvider";
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri ret = null;
        // Nos aseguramos de que la URI es correcta
        if (sURIMatcher.match(uri) != DatabaseConstants.CASE_ITEMS) {
            throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(DatabaseConstants.TABLE_ITEM, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        // ¿Se insertó correctamente?
        if (rowId != -1 ) {
            //long id = values.getAsLong(DatabaseConstants.Item.ID);
            ret = uri;
            Log.d(TAG, "uri insertada: " + ret);
            // Notificar que los datos para la URI han cambiado
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return ret;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where;
        switch (sURIMatcher.match(uri)) {
            case DatabaseConstants.CASE_ITEMS:
                where = selection;
                break;
            case DatabaseConstants.CASE_ITEM:
                long id = ContentUris.parseId(uri);
                where = DatabaseConstants.Item.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(DatabaseConstants.TABLE_ITEM, where, selectionArgs);
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros borrados: " + ret);
        db.close();
        return ret;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        String where;
        switch (sURIMatcher.match(uri)) {
            case DatabaseConstants.CASE_ITEMS:
                where = selection;
                break;
            case DatabaseConstants.CASE_ITEM:
                long id = ContentUris.parseId(uri);
                where = DatabaseConstants.Item.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(DatabaseConstants.TABLE_ITEM, values, where, selectionArgs);

        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "registros actualizados: " + ret);
        db.close();
        return ret;
    }
}
