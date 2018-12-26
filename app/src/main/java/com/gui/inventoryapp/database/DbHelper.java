package com.gui.inventoryapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gui.inventoryapp.constant.ItemConstants.ITEM;
import com.gui.inventoryapp.constant.ItemConstants;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, ItemConstants.DB_NAME, null, ItemConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + //id
                        "%s TEXT NOT NULL," + //barcode
                        "%s TEXT NOT NULL DEFAULT 'GUI'," + //owner
                        "%s DATE NOT NULL DEFAULT CURRENT_TIME," + //entry date
                        "%s INT NOT NULL DEFAULT 0," + // condition. values: -1 = broken, 0 = available, 1 = checked out
                        "%s DATE," +
                        "%s TEXT," +
                        "UNIQUE(%s))",
                ItemConstants.TABLE,
                ITEM.ID,
                ITEM.BARCODE,
                ITEM.OWNER,
                ITEM.ENTRY_DATE,
                ITEM.CONDITION,
                ITEM.CHECKOUT_EXPIRE_DATE,
                ITEM.GIVEN_TO,
                ITEM.BARCODE);
        Log.d(TAG, "onCreate con SQL: " + sql);
        db.execSQL(sql);

//        ContentValues values = new ContentValues();
//        values.put(ITEM.BARCODE)

        sql = "insert into items (barcode, owner, condition ) " +
                "values (" +
                        "'RPI-000'," + //barcode
                        "'GUI'," + //owner
                        "0);";
        db.execSQL(sql);
        sql = "insert into items (barcode, owner, condition ) " +
                "values (" +
                "'RPI-001'," + //barcode
                "'GUI'," + //owner
                "1)";
        db.execSQL(sql);
        sql = "insert into items (barcode, owner, condition ) " +
                "values (" +
                "'RPI-002'," + //barcode
                "'GUI'," + //owner
                "-1)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ItemConstants.TABLE);
        // Creamos una base de datos nueva
        onCreate(db);
        Log.d(TAG, "onUpgrade");

    }
}
