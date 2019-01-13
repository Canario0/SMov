package com.gui.inventoryapp.activities;

/**
 * @author Pablo Renero Balgañón, pabrene
 * @author Fernando Alonso Pastor, feralon
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.gui.inventoryapp.R;
import com.gui.inventoryapp.activities.fragments.AddItem;
import com.gui.inventoryapp.activities.fragments.ItemList;
import com.gui.inventoryapp.activities.fragments.LoanList;
import com.gui.inventoryapp.activities.fragments.MemberList;
import com.gui.inventoryapp.utils.interfaces.ListCommon;
import com.gui.inventoryapp.services.UpdateUsersFromRest;



public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String current_fragment;
    private DrawerLayout drawer;
    private int current_selected;
    private SearchView search_bar;
    private LinearLayout search_combo;
    private Button clear_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //iniciamos el servicio
        startService(new Intent(this, UpdateUsersFromRest.class));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        search_combo = findViewById(R.id.search_combo);

        clear_search = findViewById(R.id.clear_search_button);

        clear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchView search_bar = findViewById(R.id.search_bar);
                ListCommon x = (ListCommon) getFragmentManager().findFragmentByTag(current_fragment);
                x.reset();
                search_bar.setQuery("", false);
                search_bar.clearFocus();

            }
        });

        search_bar = findViewById(R.id.search_bar);
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                    ListCommon x = (ListCommon) getFragmentManager().findFragmentByTag(current_fragment);
                    x.update(query);
                    return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            // Crear un fragment
            ItemList fragment = new ItemList();
            current_fragment = fragment.getClass().getSimpleName();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, fragment,
                            current_fragment)
                    .commit();
            current_selected = R.id.item_list;
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.item_list) {
            if (current_selected != R.id.item_list) {
                search_combo.setVisibility(View.VISIBLE);
                ItemList fragment = new ItemList();
                current_fragment = fragment.getClass().getSimpleName();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, fragment, current_fragment
                        )
                        .commit();
                current_selected = R.id.item_list;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.add_item) {
            if (current_selected != R.id.add_item) {
                search_combo.setVisibility(View.GONE);
                AddItem fragment = new AddItem();
                current_fragment = fragment.getClass().getSimpleName();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, fragment, current_fragment)
                        .commit();
                current_selected = R.id.add_item;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.search_member) {
            if (current_selected != R.id.search_member) {
                search_combo.setVisibility(View.VISIBLE);
                MemberList fragment = new MemberList();
                current_fragment = fragment.getClass().getSimpleName();
                Log.d("LOS PACHA", current_fragment);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, fragment, current_fragment)
                        .commit();
                current_selected = R.id.search_member;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.new_checkouts) {
            if (current_selected != R.id.new_checkouts) {
                search_combo.setVisibility(View.GONE);
                LoanList fragment = new LoanList();
                current_fragment = fragment.getClass().getSimpleName();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, fragment, current_fragment)
                        .commit();
                current_selected = R.id.new_checkouts;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.close_to_end_checkouts) {
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, UpdateUsersFromRest.class));
        super.onDestroy();
    }
}
