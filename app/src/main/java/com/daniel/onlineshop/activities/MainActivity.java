package com.daniel.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.dialogs.LogoutDialog;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyMethods;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @BindView(R.id.drawer_main)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_main)
    NavigationView navigationView;

    @BindView(R.id.tv_main_user_email)
    TextView tvUserEmail;

    @OnClick(R.id.btn_store)
    public void btnStore() { startActivity(new Intent(getApplicationContext(), StoreActivity.class)); }

    @OnClick(R.id.btn_history)
    public void btnHistory() { startActivity(new Intent(getApplicationContext(), HistoryActivity.class)); }

    @OnClick(R.id.btn_statistic)
    public void btnStatistic() { startActivity(new Intent(getApplicationContext(), StatisticActivity.class)); }

    @OnClick(R.id.btn_sold_items)
    public void btnSoldItems() { startActivity(new Intent(getApplicationContext(), SoldItemsActivity.class)); }

    private MyMethods mm = new MyMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setDrawerLayout();
        setDrawerEmail();
        tvUserEmail.setText(mm.getFirebaseUserEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_log_out) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDrawerLayout() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open_drawer, R.string.nav_close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setDrawerEmail() {
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = headerView.findViewById(R.id.drawer_nav_email);
        navEmail.setText(mm.getFirebaseUserEmail());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_drawer_main:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.menu_drawer_store:
                startActivity(new Intent(getApplicationContext(), StoreActivity.class));
                break;
            case R.id.menu_drawer_history:
                startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                break;
            case R.id.menu_drawer_statistic:
                startActivity(new Intent(getApplicationContext(), StatisticActivity.class));
                break;
            case R.id.menu_drawer_items_sold:
                startActivity(new Intent(getApplicationContext(), SoldItemsActivity.class));
                break;
            case R.id.menu_drawer_logout_out:
                logout();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void logout() {
        new LogoutDialog().show(getSupportFragmentManager(), "LogoutDialog");
    }
}
