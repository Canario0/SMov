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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        getContentResolver().insert(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER),values);

        values.clear();
        values.put(DatabaseConstants.Item.BARCODE, "1234231323");
        values.put(DatabaseConstants.Item.DAMAGED, 0);
        values.put(DatabaseConstants.Item.OWNER, 12);

        getContentResolver().insert(Uri.parse(DatabaseConstants.CONTENT_URI_ITEM),values);

        values.clear();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 10);

        Date dateRepresentation = cal.getTime();



        values.put(DatabaseConstants.Loan.END_OF_LOAN, dateFormat.format(dateRepresentation));
        values.put(DatabaseConstants.Loan.ITEM, 1);
        values.put(DatabaseConstants.Loan.MEMBER, 12);

        getContentResolver().insert(Uri.parse(DatabaseConstants.CONTENT_URI_LOAN),values);




        /*
        dbHelper.getWritableDatabase().insertWithOnConflict(DatabaseConstants.TABLE_MEMBER,
                null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
        */
        EditText ed = findViewById(R.id.editText);

        Cursor cursor = getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER.toString() + "/12"),
                null,
                "",
                null,
                DatabaseConstants.Member.ALIAS);

        //Cursor cursor = dbHelper.getReadableDatabase().query(DatabaseConstants.TABLE_MEMBER,
                //null,null,null, null, null, null);
        cursor.moveToNext();
        Log.d("!--.", String.format("%d", cursor.getPosition()));
        Log.d("!--.", String.format("%d", cursor.getColumnCount()));
        Log.d("!--.", String.format("%s", cursor.getString(cursor.getColumnIndex(DatabaseConstants.Member.NAME))));
        Log.d("!DEBUG!", "NEW ROWS");

        cursor = getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_ITEM.toString()),
                null,
                "",
                null,
                null);

        cursor.moveToNext();
        Log.d("!--.", String.format("%s", cursor.getString(cursor.getColumnIndex(DatabaseConstants.Item.BARCODE))));
        Log.d("!--.", String.format("%d", cursor.getInt(cursor.getColumnIndex(DatabaseConstants.Item.ID))));


        cursor = getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_LOAN + "/" + DatabaseConstants.COUNT),
                null,
                DatabaseConstants.Loan.ITEM +"=" + "1" + " and " +DatabaseConstants.Loan.END_OF_LOAN + " < " + "CURRENT_DATE",
                null,
                null);

        cursor.moveToNext();
        Log.d("!--.", String.format("%d", cursor.getCount()));
        Log.d("!--.", String.format("COUNT = %s", cursor.getInt(0)));

    }
}
