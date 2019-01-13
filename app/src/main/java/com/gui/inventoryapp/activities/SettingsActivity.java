package com.gui.inventoryapp.activities;
/**
 * @author Pablo Renero Balgañón, pabrene
 * @author Fernando Alonso Pastor, feralon
 */
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gui.inventoryapp.activities.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Comprobar si la actividad ya ha sido creada con anterioridad
        if (savedInstanceState == null) {
            // Crear un fragment
            SettingsFragment fragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment,
                            fragment.getClass().getSimpleName())
                    .commit();
        }

    }
}
