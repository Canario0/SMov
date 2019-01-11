package com.gui.inventoryapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gui.inventoryapp.database.DatabaseConstants.Item;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, DatabaseConstants.DB_NAME, null, DatabaseConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String member_table_sql = String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY NOT NULL," + //id
                        "%s TEXT NOT NULL UNIQUE," + //alias
                        "%s TEXT NOT NULL," + //name
                        "%s TEXT NOT NULL," + //lastname
                        "%s TEXT NOT NULL," + //dni
                        "%s TEXT NOT NULL," + //email
                        "%s TEXT" + //phone
                        ")",
                DatabaseConstants.TABLE_MEMBER,
                DatabaseConstants.Member.ID,
                DatabaseConstants.Member.ALIAS,
                DatabaseConstants.Member.NAME,
                DatabaseConstants.Member.LASTNAME,
                DatabaseConstants.Member.DNI,
                DatabaseConstants.Member.EMAIL,
                DatabaseConstants.Member.PHONE);

        Log.d(TAG, "onCreate: Creando tabla Member.." + member_table_sql);
        db.execSQL(member_table_sql);

        String item_table_sql = String.format("CREATE TABLE %s (" +
                        "%s TEXT PRIMARY KEY NOT NULL," + //barcode
                        "%s INT NOT NULL," + //owner
                        "%s DATE NOT NULL DEFAULT CURRENT_DATE," + //entry date
                        "%s INT NOT NULL DEFAULT 0," + // damaged. values: 1 = broken, 0 = available
                        "FOREIGN KEY (%s) REFERENCES %s(%s))",
                DatabaseConstants.TABLE_ITEM,
                DatabaseConstants.Item.BARCODE,
                DatabaseConstants.Item.OWNER,
                DatabaseConstants.Item.ENTRY_DATE,
                DatabaseConstants.Item.DAMAGED,
                DatabaseConstants.Item.OWNER, DatabaseConstants.TABLE_MEMBER, DatabaseConstants.Member.ID);

        Log.d(TAG, "onCreate: Creando tabla Item..." + item_table_sql);
        db.execSQL(item_table_sql);

        String loan_table_sql = String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + //id
                        "%s DATE NOT NULL DEFAULT CURRENT_DATE," + //start
                        "%s DATE NOT NULL," + //end
                        "%s TEXT NOT NULL," + //member
                        "%s TEXT NOT NULL," + //item
                        "FOREIGN KEY (%s) REFERENCES %s(%s)," +
                        "FOREIGN KEY (%s) REFERENCES %s(%s))",
                DatabaseConstants.TABLE_LOAN,
                DatabaseConstants.Loan.ID,
                DatabaseConstants.Loan.START_OF_LOAN,
                DatabaseConstants.Loan.END_OF_LOAN,
                DatabaseConstants.Loan.MEMBER,
                DatabaseConstants.Loan.ITEM,
                DatabaseConstants.Loan.MEMBER, DatabaseConstants.TABLE_MEMBER, DatabaseConstants.Member.ID,
                DatabaseConstants.Loan.ITEM, DatabaseConstants.TABLE_ITEM, DatabaseConstants.Item.BARCODE);

        Log.d(TAG, "onCreate: Creando tabla Loan.." + loan_table_sql);
        db.execSQL(loan_table_sql);

        //Activamos las claves foráneas
        Log.d(TAG, "onCreate: ACTIVANDO CLAVES FORANEAS");
        db.execSQL("PRAGMA foreign_keys = ON;");

        /*
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

        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("drop table if exists " + DatabaseConstants.TABLE);
        // Creamos una base de datos nueva
        onCreate(db);
        Log.d(TAG, "onUpgrade");

    }
}
