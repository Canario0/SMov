package com.gui.inventoryapp.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Log;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.database.DatabaseConstants;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UpdateUsersFromRest extends IntentService {

    static final String TAG = "UpdateUsersFromRest";
    static final int DELAY = 180000;
    private boolean runFlag = false;
    static String default_endpoint = "https://gui.uva.es/guinetv3/";

    public UpdateUsersFromRest() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Updating users from rest");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String main_url = prefs.getString("endpoint", default_endpoint);
        runFlag = true;
        while (runFlag) {
            // Create URL
            URL githubEndpoint = null;
            try {
                githubEndpoint = new URL("https://gui.uva.es/guinetv3/members");

                HttpsURLConnection myConnection = null;
                // Create connection
                myConnection =
                        (HttpsURLConnection) githubEndpoint.openConnection();


                myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
                myConnection.setRequestProperty("Accept",
                        "application/vnd.github.v3+json");

                if (myConnection.getResponseCode() == 200) {
                    Log.d(TAG, "Conexión buena");
                } else {
                    Log.d(TAG, "Rechazada!");
                }
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader =
                        new InputStreamReader(responseBody, "UTF-8");

                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(responseBodyReader);

                //Erase database except two first items

                /*
                String where = DatabaseConstants.Member.ID + "> 1";
                getContentResolver().delete(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER),where,null);
                 */

                ContentValues values = new ContentValues();

                for(int i = 0; i < jsonArray.size(); i++ ){
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    values.clear();
                    values.put(DatabaseConstants.Member.ALIAS, (String)obj.get(DatabaseConstants.Member.ALIAS));
                    values.put(DatabaseConstants.Member.DNI, (String)obj.get(DatabaseConstants.Member.DNI));
                    values.put(DatabaseConstants.Member.NAME, (String)obj.get(DatabaseConstants.Member.NAME));
                    values.put(DatabaseConstants.Member.LASTNAME, (String)obj.get(DatabaseConstants.Member.LASTNAME));
                    values.put(DatabaseConstants.Member.EMAIL, (String)obj.get(DatabaseConstants.Member.EMAIL));
                    values.put(DatabaseConstants.Member.PHONE, (String)obj.get(DatabaseConstants.Member.PHONE));

                    Uri uri = getContentResolver().insert(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER),values);

                    if(uri == null){
                        Log.d(TAG, "User is on db.. Updating...");

                        values.clear();
                        values.put(DatabaseConstants.Member.NAME, (String)obj.get(DatabaseConstants.Member.NAME));
                        values.put(DatabaseConstants.Member.LASTNAME, (String)obj.get(DatabaseConstants.Member.LASTNAME));
                        values.put(DatabaseConstants.Member.EMAIL, (String)obj.get(DatabaseConstants.Member.EMAIL));
                        values.put(DatabaseConstants.Member.PHONE, (String)obj.get(DatabaseConstants.Member.PHONE));

                        String where = DatabaseConstants.Member.ALIAS + " ='" + (String)obj.get(DatabaseConstants.Member.ALIAS)+"'";

                        getContentResolver().update(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER),values,where,null);
                    }
                }

                Thread.sleep(DELAY);
            } catch (MalformedURLException e) {
                Log.d(TAG, e.getMessage());
                return;
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
                return;
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
                return;
            } catch (InterruptedException e) {
                Log.d(TAG, e.getMessage());
                return;
            } catch (Exception e) {
                Log.d(TAG,"Generic exception -> " +  e.getMessage());
                return;
            }


        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.runFlag = false;
        Log.d(TAG, "onDestroy()");
    }
}
