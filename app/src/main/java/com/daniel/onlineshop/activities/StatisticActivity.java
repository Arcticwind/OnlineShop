package com.daniel.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.adapters.StatisticAdapter;
import com.daniel.onlineshop.dialogs.DateDialog;
import com.daniel.onlineshop.dialogs.LastEntriesDialog;
import com.daniel.onlineshop.dialogs.LogoutDialog;
import com.daniel.onlineshop.dialogs.MonthPickerDialog;
import com.daniel.onlineshop.models.StatisticItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyDateFormats;
import com.daniel.onlineshop.utils.MyMethods;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_statistic)
    Toolbar toolbar;

    @BindView(R.id.drawer_statistic)
    DrawerLayout drawerLayout;

    @BindView(R.id.rv_statistic)
    RecyclerView recyclerView;

    @BindView(R.id.nav_statistic)
    NavigationView navigationView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference statisticRef = db.collection(MyConstants.COLLECTION_STATISTIC_NAME);
    private StatisticAdapter adapter;
    private MyDateFormats df = new MyDateFormats();
    private MyMethods mm = new MyMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        setDrawerLayout();
        setDrawerEmail();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = statisticRef.orderBy(MyConstants.STATISTIC_ORDER_BY, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<StatisticItem> options = new FirestoreRecyclerOptions.Builder<StatisticItem>()
                .setQuery(query, StatisticItem.class)
                .build();

        adapter = new StatisticAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(StatisticActivity.this));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
    }

    public StatisticAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_statistic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_current_month:
                actionEntriesCurrentMonth();
                break;
            case R.id.menu_search_month:
                actionEntriesByMonth();
                break;
            case R.id.menu_search_date:
                actionEntriesByDate();
                break;
            case R.id.menu_show_last:
                actionEntriesShowLast();
                break;
            case R.id.menu_show_all:
                actionEntriesShowAll();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionEntriesCurrentMonth() {
        String month = df.getMonth();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference statisticRef = db.collection(MyConstants.COLLECTION_STATISTIC_NAME);
        Query query = statisticRef
                .whereEqualTo(MyConstants.STATISTIC_KEY_MONTH, month)
                .orderBy(MyConstants.STATISTIC_ORDER_BY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<StatisticItem> options = new FirestoreRecyclerOptions.Builder<StatisticItem>()
                .setQuery(query, StatisticItem.class)
                .build();
        getAdapter().updateOptions(options);
    }

    private void actionEntriesByMonth() {
        new MonthPickerDialog().show(getSupportFragmentManager(), "MonthPickerDialog");
    }

    private void actionEntriesByDate() {
        new DateDialog().show(getSupportFragmentManager(), "DateDialog");
    }

    private void actionEntriesShowLast() {
        new LastEntriesDialog().show(getSupportFragmentManager(), "LastEntriesDialog");
    }

    private void actionEntriesShowAll() {
        Query query = statisticRef
                .orderBy(MyConstants.STATISTIC_ORDER_BY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<StatisticItem> options = new FirestoreRecyclerOptions.Builder<StatisticItem>()
                .setQuery(query, StatisticItem.class)
                .build();
        adapter.updateOptions(options);
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

    private void logout() {
        new LogoutDialog().show(getSupportFragmentManager(), "LogoutDialog");
    }
}
