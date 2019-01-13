package com.gui.inventoryapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.gui.inventoryapp.database.DatabaseConstants.Item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, DatabaseConstants.DB_NAME, null, DatabaseConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String member_table_sql = String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + //id
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
                        " %s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + //id
                        " %s TEXT NOT NULL UNIQUE," + //barcode
                        " %s INT NOT NULL," + //owner
                        " %s DATE NOT NULL DEFAULT CURRENT_DATE," + //entry date
                        " %s INT NOT NULL DEFAULT 0," + // damaged. values: 1 = broken, 0 = available
                        "FOREIGN KEY (%s) REFERENCES %s(%s))",
                DatabaseConstants.TABLE_ITEM,
                DatabaseConstants.Item.ID,
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
                        "%s INTEGER NOT NULL DEFAULT 0," + //returned (0-not, 1-yes)
                        "%s INTEGER NOT NULL," + //member
                        "%s INTEGER NOT NULL," + //item
                        "FOREIGN KEY (%s) REFERENCES %s(%s)," +
                        "FOREIGN KEY (%s) REFERENCES %s(%s))",
                DatabaseConstants.TABLE_LOAN,
                DatabaseConstants.Loan.ID,
                DatabaseConstants.Loan.START_OF_LOAN,
                DatabaseConstants.Loan.END_OF_LOAN,
                DatabaseConstants.Loan.RETURNED,
                DatabaseConstants.Loan.MEMBER,
                DatabaseConstants.Loan.ITEM,
                DatabaseConstants.Loan.MEMBER, DatabaseConstants.TABLE_MEMBER, DatabaseConstants.Member.ID,
                DatabaseConstants.Loan.ITEM, DatabaseConstants.TABLE_ITEM, DatabaseConstants.Item.ID);

        Log.d(TAG, "onCreate: Creando tabla loan.." + loan_table_sql);
        db.execSQL(loan_table_sql);

        //Activamos las claves foráneas
        Log.d(TAG, "onCreate: ACTIVANDO CLAVES FORANEAS");
        db.execSQL("PRAGMA foreign_keys = ON;");


        /* Datos de prueba */
        ContentValues values = new ContentValues();

        values.clear();
        values.put(DatabaseConstants.Member.ID, 1);
        values.put(DatabaseConstants.Member.ALIAS, "MIGUELIO");
        values.put(DatabaseConstants.Member.DNI, "1223R");
        values.put(DatabaseConstants.Member.NAME, "Miguel");
        values.put(DatabaseConstants.Member.LASTNAME, "Ranero");
        values.put(DatabaseConstants.Member.EMAIL, "miguel@uva.es");
        values.put(DatabaseConstants.Member.PHONE, "+1555123453");

        db.insertWithOnConflict(DatabaseConstants.TABLE_MEMBER, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);


        values.clear();
        values.put(DatabaseConstants.Member.ID, 0);
        values.put(DatabaseConstants.Member.ALIAS, "gui");
        values.put(DatabaseConstants.Member.DNI, "0");
        values.put(DatabaseConstants.Member.NAME, "Grupo Universitario Informática");
        values.put(DatabaseConstants.Member.LASTNAME, "");
        values.put(DatabaseConstants.Member.EMAIL, "");
        values.put(DatabaseConstants.Member.PHONE, "");

        db.insertWithOnConflict(DatabaseConstants.TABLE_MEMBER, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        values.clear();
        values.put(DatabaseConstants.Item.BARCODE, "RPI-000");
        values.put(DatabaseConstants.Item.DAMAGED, 0);
        values.put(DatabaseConstants.Item.OWNER, 0);

        db.insertWithOnConflict(DatabaseConstants.TABLE_ITEM, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 17);

        Date dateRepresentation = cal.getTime();

        values.clear();
        values.put(DatabaseConstants.Loan.END_OF_LOAN, dateFormat.format(dateRepresentation));
        values.put(DatabaseConstants.Loan.ITEM, 1);
        values.put(DatabaseConstants.Loan.MEMBER, 1);

        db.insertWithOnConflict(DatabaseConstants.TABLE_LOAN, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");

    }
}
