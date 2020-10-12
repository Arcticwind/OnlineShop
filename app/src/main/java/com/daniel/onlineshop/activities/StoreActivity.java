package com.daniel.onlineshop.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.adapters.OnItemClickListener;
import com.daniel.onlineshop.adapters.StoreAdapter;
import com.daniel.onlineshop.dialogs.LogoutDialog;
import com.daniel.onlineshop.models.HistoryItem;
import com.daniel.onlineshop.models.StatisticItem;
import com.daniel.onlineshop.models.StoreItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyDateFormats;
import com.daniel.onlineshop.utils.MyMethods;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_store)
    Toolbar toolbar;

    @BindView(R.id.drawer_store)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_store)
    NavigationView navigationView;

    @BindView(R.id.rv_store)
    RecyclerView recyclerView;

    @OnClick(R.id.fab_add_item)
    public void fabAddItem() {
        startActivityForResult(new Intent(getApplicationContext(), AddEditItemActivity.class), MyConstants.REQUEST_ADD_ITEM);
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemRef = db.collection(MyConstants.COLLECTION_ITEM_NAME);
    private CollectionReference historyRef = db.collection(MyConstants.COLLECTION_HISTORY_NAME);
    private CollectionReference statisticRef = db.collection(MyConstants.COLLECTION_STATISTIC_NAME);
    private StoreAdapter adapter;
    private StoreItem storeItem = new StoreItem();
    private StorageReference storageRef;
    private MyDateFormats df = new MyDateFormats();
    private MyMethods mm = new MyMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setDrawerLayout();
        setDrawerEmail();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = itemRef.orderBy(MyConstants.ITEM_NAME_ORDER_BY, Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<StoreItem> options = new FirestoreRecyclerOptions.Builder<StoreItem>()
                .setQuery(query, StoreItem.class)
                .build();
        adapter = new StoreAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        enableSwipeFunctionality();

        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                itemRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        updateStoreItem(documentSnapshot);
                    }
                });
            }
        });
    }

    private void enableSwipeFunctionality() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (isNetworkConnected()) {
                    deleteStoreItem(viewHolder);
                } else {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    Toast.makeText(StoreActivity.this, getString(R.string.toast_connect_internet), Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void deleteStoreItem(RecyclerView.ViewHolder viewHolder) {
        StoreItem storeItem = adapter.getItem(viewHolder.getAdapterPosition());
        adapter.deleteItem(viewHolder.getAdapterPosition());
        createDeleteHistoryEntry(storeItem);
        deleteToStatistic();
        deleteImageFromStorage(storeItem);
    }

    private void deleteImageFromStorage(StoreItem storeItem) {
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storeItem.getImageUrl());
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_item_image_deleted), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStoreItem(DocumentSnapshot documentSnapshot) {
        StoreItem item = documentSnapshot.toObject(StoreItem.class);
        String id = documentSnapshot.getId();
        Intent intent = new Intent(getApplicationContext(), AddEditItemActivity.class);
        intent.putExtra(MyConstants.EXTRA_STORE_ITEM_OBJECT, item);
        intent.putExtra(MyConstants.EXTRA_ID, id);
        startActivityForResult(intent, MyConstants.REQUEST_EDIT_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MyConstants.REQUEST_ADD_ITEM && resultCode == RESULT_OK && data != null) {
            actionAddItem(data);
            createAddHistoryEntry(storeItem);
            addToStatistic();

        } else if (requestCode == MyConstants.REQUEST_EDIT_ITEM && resultCode == RESULT_OK && data != null) {
            actionUpdateItem(data);
            createUpdateHistoryEntry(storeItem);
            updateToStatistic();
        }
    }

    private void actionAddItem(Intent data) {
        String name = data.getExtras().getString(MyConstants.EXTRA_TITLE);
        String description = data.getExtras().getString(MyConstants.EXTRA_DESCRIPTION);
        double price = data.getExtras().getDouble(MyConstants.EXTRA_PRICE);
        String imageUrl = data.getExtras().getString(MyConstants.EXTRA_IMAGE_URL);
        int quantity = data.getExtras().getInt(MyConstants.EXTRA_QUANTITY);

        storeItem = new StoreItem(name, description, price, imageUrl, quantity);
        itemRef.document(name).set(storeItem);
        Toast.makeText(this, R.string.toast_item_added, Toast.LENGTH_SHORT).show();
    }

    private void actionUpdateItem(Intent data) {
        String id = data.getExtras().getString(MyConstants.EXTRA_ID);
        String name = data.getExtras().getString(MyConstants.EXTRA_TITLE);
        String description = data.getExtras().getString(MyConstants.EXTRA_DESCRIPTION);
        double price = data.getExtras().getDouble(MyConstants.EXTRA_PRICE);
        String imageUrl = data.getExtras().getString(MyConstants.EXTRA_IMAGE_URL);
        int quantity = data.getExtras().getInt(MyConstants.EXTRA_QUANTITY);

        storeItem = new StoreItem(name, description, price, imageUrl, quantity);

        itemRef.document(id).delete();
        itemRef.document(name).set(storeItem);
        Toast.makeText(this, R.string.toast_item_updated, Toast.LENGTH_SHORT).show();
    }


    private void createAddHistoryEntry(StoreItem storeItem) {
        historyRef.add(new HistoryItem(storeItem.getTitle(), mm.getFirebaseUserEmail(), MyConstants.OPERATION_ADD, df.getDateTime(), storeItem.getPrice()));
    }

    private void createUpdateHistoryEntry(StoreItem storeItem) {
        historyRef.add(new HistoryItem(storeItem.getTitle(), mm.getFirebaseUserEmail(), MyConstants.OPERATION_UPDATE, df.getDateTime(), storeItem.getPrice()));
    }

    private void createDeleteHistoryEntry(StoreItem storeItem) {
        historyRef.add(new HistoryItem(storeItem.getTitle(), mm.getFirebaseUserEmail(), MyConstants.OPERATION_DELETE, df.getDateTime(), storeItem.getPrice()));
    }

    private void createNewStatisticEntry() {
        StatisticItem statisticItem = new StatisticItem(df.getYear(), df.getMonth(), df.getDay(), 1, 0, 0);
        statisticRef.document(df.getDate()).set(statisticItem);
    }

    private void addToStatistic() {
        statisticRef.document(df.getDate()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    addExistingStatistic(task.getResult());
                } else {
                    createNewStatisticEntry();
                    addToStatistic();
                }
            }
        });
    }

    private void addExistingStatistic(DocumentSnapshot ds) {
        StatisticItem si = ds.toObject(StatisticItem.class);
        StatisticItem statisticItem = new StatisticItem(df.getYear(), df.getMonth(), df.getDay(), si.getAddCount()+1, si.getUpdateCount(), si.getDeleteCount());
        statisticRef.document(df.getDate()).set(statisticItem);
    }

    private void updateToStatistic() {
        statisticRef.document(df.getDate()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    updateExistingStatistic(task.getResult());
                } else {
                    createNewStatisticEntry();
                    updateToStatistic();
                }
            }
        });
    }

    private void updateExistingStatistic(DocumentSnapshot ds) {
        StatisticItem si = ds.toObject(StatisticItem.class);
        StatisticItem statisticItem = new StatisticItem(df.getYear(), df.getMonth(), df.getDay(), si.getAddCount(), si.getUpdateCount()+1, si.getDeleteCount());
        statisticRef.document(df.getDate()).set(statisticItem);
    }

    private void deleteToStatistic() {
        statisticRef.document(df.getDate()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    deleteExistingStatistic(task.getResult());
                } else {
                    createNewStatisticEntry();
                    deleteToStatistic();
                }
            }
        });
    }

    private void deleteExistingStatistic(DocumentSnapshot ds) {
        StatisticItem si = ds.toObject(StatisticItem.class);
        StatisticItem statisticItem = new StatisticItem(df.getYear(), df.getMonth(), df.getDay(), si.getAddCount(), si.getUpdateCount(), si.getDeleteCount()+1);
        statisticRef.document(df.getDate()).set(statisticItem);
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

    private void logout() {
        new LogoutDialog().show(getSupportFragmentManager(), "LogoutDialog");
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
}
