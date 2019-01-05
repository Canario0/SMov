package com.gui.inventoryapp.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.database.DatabaseConstants;
import com.gui.inventoryapp.database.DbHelper;

public class TESTING extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        DbHelper dbHelper = new DbHelper(getApplicationContext());

        ContentValues values = new ContentValues();

        values.clear();
        values.put(DatabaseConstants.Member.ID, 12);
        values.put(DatabaseConstants.Member.ALIAS, "CANARIO");
        values.put(DatabaseConstants.Member.DNI, "1223R");
        values.put(DatabaseConstants.Member.NAME, "Miguel");
        values.put(DatabaseConstants.Member.LASTNAME, "Ranero");
        values.put(DatabaseConstants.Member.EMAIL, "miguel@uva.es");
        values.put(DatabaseConstants.Member.PHONE, "+1555123453");

        dbHelper.getWritableDatabase().insertWithOnConflict(DatabaseConstants.TABLE_MEMBER,
                null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        Log.d("!DEBUG!", "NEW ROWS");

    }
}
