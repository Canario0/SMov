package com.gui.inventoryapp.fragments;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import com.gui.inventoryapp.constant.ItemConstants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddItem extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private EditText barcode_et;
    private EditText date_et;
    private Spinner owner_sp;
    private ImageView scan_btn;
    private Button accept_btn;
    private Button cancel_btn;

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

                ContentValues values = new ContentValues();
                values.put(ItemConstants.ITEM.BARCODE, barcode_et.getText().toString());
                values.put(ItemConstants.ITEM.ENTRY_DATE, date_et.getText().toString());
                values.put(ItemConstants.ITEM.OWNER, (String) owner_sp.getSelectedItem());
                Uri out = getActivity().getContentResolver().insert(
                        ItemConstants.CONTENT_URI,   // the user dictionary content URI
                        values                       // the columns to update
                );

                if (out != null) {
                    Toast.makeText(getContext(), "Item Añadido", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getContext(), "El Item ya existe", Toast.LENGTH_LONG).show();
                }
                date_et.setText("");
                barcode_et.setText("");
                owner_sp.setSelection(0);
            }
        });

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implementar el scaneo
            }
        });

        return view;
    }

}

