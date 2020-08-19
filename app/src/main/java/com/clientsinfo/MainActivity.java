package com.clientsinfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.clientsinfo.parsemanagement.ParseManagement;
import com.clientsinfo.recyclerviewinterfaces.FilterableItems;
import com.clientsinfo.recyclerviewinterfaces.RecyclerViewSetter;
import com.clientsinfo.ui.purchases.PurchaseFilterDialog;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FilterableItems {

    private AppBarConfiguration mAppBarConfiguration;
    private FilterableItems filterableItems;
    public static final String SHOW_ALL = "Show all";
    public static final int LOG_OUT = 2;
    private String selectedClient = SHOW_ALL;
    private Fragment currentFragment;
    private Menu optionsMenu;
    private RecyclerViewSetter recyclerViewSetter;

    @BindView(R.id.name_text)
    TextView userName;

    @BindView(R.id.email_text)
    TextView email;

    private DrawerLayout drawer;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);

        ButterKnife.bind(this, navigationView.getHeaderView(0));

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_purchases, R.id.nav_clients, R.id.nav_accounting)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Initialize user information in the drawer

        ParseUser currentUser = ParseUser.getCurrentUser();
        userName.setText(currentUser.getUsername());
        email.setText(currentUser.getEmail());

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.sync_data_on_start);
        boolean defaultValue = getResources().getBoolean(R.bool.sync_preference_default_value);
        boolean sync = sharedPreferences.getBoolean(key, defaultValue);

        setRecyclerViewSetter((RecyclerViewSetter) getCurrentFragment());

        if (sync)
            getData();
        else
            ParseManagement.initializeClientsMenu(navigationView);

    }

    public void getData() {
        ParseManagement.initializeLocalDataStore(this,
                getSupportFragmentManager(),
                getNavigationView(),
                recyclerViewSetter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        optionsMenu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.sync:
                getData();
                return true;

            case R.id.show_all:
                selectedClient = SHOW_ALL;
                filterItems(null);
                return true;

            case R.id.filter:
                new PurchaseFilterDialog().show(getSupportFragmentManager(), "dialog");
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:
                parseLogout();
                return true;

        }

        return super.onOptionsItemSelected(item);

    }


    private void parseLogout() {

        LoadingDialog loadingDialog = new LoadingDialog("Logging out ...");
        loadingDialog.show(getSupportFragmentManager(), "dialog");
        loadingDialog.setCancelable(false);

        ParseQuery<ParseUser> query = ParseQuery.getQuery("User");
        query.findInBackground((objects, e) -> {
            loadingDialog.dismiss();

            if (e != null) {
                if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    new AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Connection problem")
                            .setMessage("Failed to logout. Please make sure you have an internet connection and try again.")
                            .setPositiveButton("Retry", (dialogInterface, i) -> MainActivity.this.parseLogout())
                            .setNegativeButton("Cancel", ((dialogInterface, i) -> {
                            })).show();
                }
            } else {
                ParseUser.logOutInBackground(e1 -> {
                    MainActivity.this.setResult(LOG_OUT);
                    MainActivity.this.finish();
                });
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void filterItems(String name) {
        filterableItems.filterItems(name);
    }

    @Override
    public void filterItems(String name, Date startDate, Date endDate) {
        filterableItems.filterItems(name, startDate, endDate);
    }

    public void setFilterableItems(FilterableItems filterableItems) {
        this.filterableItems = filterableItems;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public String getSelectedClient() {
        return this.selectedClient;
    }

    public void setSelectedClient(String selectedClient) {
        this.selectedClient = selectedClient;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public void setRecyclerViewSetter(RecyclerViewSetter recyclerViewSetter) {
        this.recyclerViewSetter = recyclerViewSetter;
    }

    @Override
    public void onBackPressed() {
        setBackPressed();
    }

    private void setBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            finish();
    }

    public void purchasesFragmentOptionsSetup() {
        if (optionsMenu != null) {
            optionsMenu.getItem(0).setVisible(true);
            optionsMenu.getItem(1).setVisible(true);
            optionsMenu.getItem(2).setVisible(true);
        }
    }


    public void clientsFragmentOptionsSetup() {
        optionsMenu.getItem(0).setVisible(true);
        optionsMenu.getItem(1).setVisible(false);
        optionsMenu.getItem(2).setVisible(true);
    }

    public void accountingFragmentOptionsSetup() {
        optionsMenu.getItem(0).setVisible(false);
        optionsMenu.getItem(1).setVisible(false);
        optionsMenu.getItem(2).setVisible(false);
    }

}