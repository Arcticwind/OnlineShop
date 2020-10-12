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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.adapters.HistoryAdapter;
import com.daniel.onlineshop.dialogs.ClearHistoryDialog;
import com.daniel.onlineshop.dialogs.LogoutDialog;
import com.daniel.onlineshop.models.HistoryItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyMethods;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_history)
    Toolbar toolbar;

    @BindView(R.id.drawer_history)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_history)
    NavigationView navigationView;

    @BindView(R.id.rv_history)
    RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection(MyConstants.COLLECTION_HISTORY_NAME);
    MyMethods mm = new MyMethods();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        setDrawerLayout();
        setDrawerEmail();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = historyRef.orderBy(MyConstants.HISTORY_ORDER_BY, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HistoryItem> options = new FirestoreRecyclerOptions.Builder<HistoryItem>()
                .setQuery(query, HistoryItem.class)
                .build();

        adapter = new HistoryAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_history_clear) {
            clearHistory();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearHistory() {
        new ClearHistoryDialog().show(getSupportFragmentManager(), "ClearHistoryDialog");
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
