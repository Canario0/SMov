package com.gui.inventoryapp.activities.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.database.DatabaseConstants;
import com.gui.inventoryapp.utils.BarcodeScanner;

import java.util.ArrayList;
import java.util.List;

import static com.gui.inventoryapp.utils.GeneralConstants.CAMERA_REQUEST;
import static com.gui.inventoryapp.utils.GeneralConstants.MY_CAMERA_PERMISSION_CODE;

public class AddItem extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private EditText barcode_et;
    private Spinner owner_sp;
    private ImageView scan_btn;
    private Button accept_btn;
    private Button cancel_btn;
    private BarcodeScanner barscan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_item, container, false);
        barcode_et = view.findViewById(R.id.barcode_input);
        owner_sp = view.findViewById(R.id.owner_spinner);
        scan_btn = view.findViewById(R.id.scan_barcode);
        accept_btn = view.findViewById(R.id.add_item_button);
        cancel_btn = view.findViewById(R.id.cancel_button);
        barscan = new BarcodeScanner(this);

        final String split_token = "- ";

        List<String> list = new ArrayList<String>();

        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(DatabaseConstants.CONTENT_URI_MEMBER),
                null,
                null,
                null,
                DatabaseConstants.Member.ID);

        while(cursor.moveToNext()){
            list.add(cursor.getLong(cursor.getColumnIndex(DatabaseConstants.Member.ID)) + split_token + cursor.getString(cursor.getColumnIndex(DatabaseConstants.Member.ALIAS)) );
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        owner_sp.setAdapter(dataAdapter);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcode_et.setText("");
                owner_sp.setSelection(0);
            }
        });

        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (barcode_et.getText().toString().equals("")) {
                    Toast.makeText(getContext(), R.string.warning_barcode_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.Item.BARCODE, barcode_et.getText().toString());

                long owner_code =
                        Long.parseLong(((String) owner_sp.getSelectedItem()).split(split_token)[0]);

                Log.d(TAG,owner_code + " ");

                values.put(DatabaseConstants.Item.OWNER, owner_code);


                Uri out = getActivity().getContentResolver().insert(
                        Uri.parse(DatabaseConstants.CONTENT_URI_ITEM),// the user dictionary content URI
                        values // the columns to update
                );

                if (out != null) {
                    Toast.makeText(getContext(), R.string.item_added, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.warning_item_exists, Toast.LENGTH_LONG).show();
                }
                barcode_et.setText("");
                owner_sp.setSelection(0);
            }
        });

        /* Boton de escaneado de código */
        scan_btn.setOnClickListener(barscan);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case MY_CAMERA_PERMISSION_CODE:
                scan_btn.performClick();
                break;

            default:
                throw new UnsupportedOperationException();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //No se realiza ninguna acción
        if(resultCode != Activity.RESULT_OK)
            return;

        switch(requestCode) {
            case CAMERA_REQUEST:
                String str = BarcodeScanner.getBarCode(barscan.getPath(),getContext());
                if(str != null)
                    barcode_et.setText(str); //Colocamos el texto en el campo
                else
                    Toast.makeText(getContext(), R.string.code_bar_not_recognized, Toast.LENGTH_LONG).show();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }




}

