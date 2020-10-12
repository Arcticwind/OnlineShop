package com.daniel.onlineshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.adapters.SoldItemsAdapter;
import com.daniel.onlineshop.dialogs.LogoutDialog;
import com.daniel.onlineshop.models.SoldItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyMethods;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SoldItemsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_sold_items)
    Toolbar toolbar;

    @BindView(R.id.drawer_sold_items)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_sold_items)
    NavigationView navigationView;

    @BindView(R.id.rv_sold_items)
    RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MyMethods mm = new MyMethods();
    private CollectionReference soldItemsRef = db.collection(MyConstants.COLLECTION_SOLD_ITEMS);
    private SoldItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_items);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setDrawerLayout();
        setDrawerEmail();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerView();
    }


    private void setUpRecyclerView() {

        Query query = soldItemsRef.orderBy("priceTotal", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<SoldItem> options = new FirestoreRecyclerOptions.Builder<SoldItem>()
                .setQuery(query, SoldItem.class)
                .build();

        adapter = new SoldItemsAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setDrawerEmail() {
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = headerView.findViewById(R.id.drawer_nav_email);
        navEmail.setText(mm.getFirebaseUserEmail());
    }

    private void setDrawerLayout() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open_drawer, R.string.nav_close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
