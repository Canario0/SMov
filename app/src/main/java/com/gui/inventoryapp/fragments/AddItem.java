package com.gui.inventoryapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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
import com.gui.inventoryapp.utils.BarcodeScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static com.gui.inventoryapp.utils.GeneralConstants.CAMERA_REQUEST;
import static com.gui.inventoryapp.utils.GeneralConstants.MY_CAMERA_PERMISSION_CODE;

public class AddItem extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private EditText barcode_et;
    private EditText date_et;
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
        date_et = view.findViewById(R.id.input_date);
        owner_sp = view.findViewById(R.id.owner_spinner);
        scan_btn = view.findViewById(R.id.scan_barcode);
        accept_btn = view.findViewById(R.id.add_item_button);
        cancel_btn = view.findViewById(R.id.cancel_button);
        barscan = new BarcodeScanner(this);

        //TODO: cambiar los ejemplos por datos de la base de datos.
        List<String> list = new ArrayList<String>();
        list.add("GUI");
        list.add("Fernando");
        list.add("Pablo");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        owner_sp.setAdapter(dataAdapter);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_et.setText("");
                barcode_et.setText("");
                owner_sp.setSelection(0);
            }
        });

        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (barcode_et.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Por favor rellene el Barcode.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (date_et.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Por favor rellene la Fecha.", Toast.LENGTH_LONG).show();
                    return;
                }
                /*
                ContentValues values = new ContentValues();
                values.put(ItemConstants.ITEM.BARCODE, barcode_et.getText().toString());
                values.put(ItemConstants.ITEM.ENTRY_DATE, date_et.getText().toString());
                values.put(ItemConstants.ITEM.OWNER, (String) owner_sp.getSelectedItem());
                Uri out = getActivity().getContentResolver().insert(
                        ItemConstants.CONTENT_URI,   // the user dictionary content URI
                        values                       // the columns to update
                );
                *
                if (out != null) {
                    Toast.makeText(getContext(), "Item Añadido", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getContext(), "El Item ya existe", Toast.LENGTH_LONG).show();
                }
                */
                date_et.setText("");
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
                    Toast.makeText(getContext(), "No se reconoce ningún código de barras code_39", Toast.LENGTH_LONG).show();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }




}

