package com.gui.inventoryapp.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

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

        getContentResolver().insert(DatabaseConstants.CONTENT_URI_MEMBER,values);

        /*
        dbHelper.getWritableDatabase().insertWithOnConflict(DatabaseConstants.TABLE_MEMBER,
                null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
        */
        EditText ed = findViewById(R.id.editText);

        Cursor cursor = getContentResolver().query(DatabaseConstants.CONTENT_URI_MEMBER,
                null,
                "",
                null,
                DatabaseConstants.Member.ALIAS);

        //Cursor cursor = dbHelper.getReadableDatabase().query(DatabaseConstants.TABLE_MEMBER,
                null,null,null, null, null, null);
        Log.d("!--.", String.format("%d", cursor.getCount()));
        Log.d("!DEBUG!", "NEW ROWS");

    }
}
