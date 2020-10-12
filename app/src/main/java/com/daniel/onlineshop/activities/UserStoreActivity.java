package com.daniel.onlineshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.adapters.OnItemClickListener;
import com.daniel.onlineshop.adapters.UserStoreAdapter;
import com.daniel.onlineshop.dialogs.CurrencyDialog;
import com.daniel.onlineshop.dialogs.DialogCallback;
import com.daniel.onlineshop.dialogs.LogoutDialog;
import com.daniel.onlineshop.dialogs.SortDialog;
import com.daniel.onlineshop.models.StoreItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyMethods;
import com.daniel.onlineshop.webservices.currencyconverter.ConverterInterface;
import com.daniel.onlineshop.webservices.currencyconverter.Converter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserStoreActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_user_store)
    Toolbar toolbar;

    @BindView(R.id.rv_user_store)
    RecyclerView recyclerView;

    @BindView(R.id.drawer_user_store)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_user_store)
    NavigationView navigationView;

    private ConverterInterface converterInterface;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference storeRef = db.collection(MyConstants.COLLECTION_ITEM_NAME);
    private UserStoreAdapter adapter;
    private Boolean adapterExists = false;
    private Map<String, Double> currencies = new HashMap<>();
    private SharedPreferences sp;
    private String activeCurrencyName;
    private Double activeCurrencyValue;
    private Query query;
    private FirestoreRecyclerOptions<StoreItem> options;
    private MyMethods mm = new MyMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_store);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setDrawerLayout();
        setDrawerEmail();

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        getCurrencyRates();
    }

    private void getCurrencyRates() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(converterInterface.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        converterInterface = retrofit.create(ConverterInterface.class);

        Call<Converter> call = converterInterface.getRates(converterInterface.baseCurrencyName);

        call.enqueue(new Callback<Converter>() {
            @Override
            public void onResponse(Call<Converter> call, Response<Converter> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                }

                Converter converter = response.body();
                createCurrencyMap(converter);
                setUpRecyclerView();
            }

            @Override
            public void onFailure(Call<Converter> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView() {
        query = storeRef.orderBy(MyConstants.ITEM_NAME_ORDER_BY, Query.Direction.ASCENDING);

        buildStoreItemRecycler(query);

        activeCurrencyName = sp.getString(MyConstants.CURRENCY_NAME, null);
        activeCurrencyValue = currencies.get(activeCurrencyName);

        if (activeCurrencyValue == null) {
            activeCurrencyValue = 1.0;
            activeCurrencyName = "EUR";
        }

        adapter = new UserStoreAdapter(options, getApplicationContext(), activeCurrencyValue, activeCurrencyName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapterExists = true;

        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot ds) {
                toItemDetails(ds);
            }
        });
    }

    private void buildStoreItemRecycler(Query query) {
        options = new FirestoreRecyclerOptions.Builder<StoreItem>()
                .setQuery(query, StoreItem.class)
                .build();
    }

    private void toItemDetails(DocumentSnapshot ds) {
        StoreItem item = ds.toObject(StoreItem.class);
        Intent intent = new Intent(getApplicationContext(), ItemDetailsActivity.class);
        intent.putExtra(MyConstants.EXTRA_ID, item.getTitle());
        intent.putExtra(MyConstants.CURRENCY_NAME, activeCurrencyName);
        intent.putExtra(MyConstants.CURRENCY_VALUE, activeCurrencyValue);
        startActivity(intent);
    }

    private void createCurrencyMap(Converter converter) {
        currencies.put(MyConstants.EUR, 1.0);
        currencies.put(MyConstants.HRK, converter.getRates().getHRK());
        currencies.put(MyConstants.CHF, converter.getRates().getCHF());
        currencies.put(MyConstants.USD, converter.getRates().getUSD());
    }

    public UserStoreAdapter getAdapter() {
        return adapter;
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
            case R.id.menu_drawer_user_sort:
                showSortMenuDialog();
                break;
            case R.id.menu_drawer_user_cart:
                startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
                break;
            case R.id.menu_drawer_user_receipts:
                startActivity(new Intent(getApplicationContext(), ReceiptActivity.class));
                break;
            case R.id.menu_drawer_user_logout_out:
                logout();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapterExists) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_store, menu);
        setUpSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setUpSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.menu_user_store_search_view);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setUpRecyclerView();
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String q) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String q) {
        Query query = storeRef
                .orderBy(MyConstants.ITEM_NAME_ORDER_BY, Query.Direction.DESCENDING)
                .whereEqualTo(MyConstants.ITEM_NAME, q);

        buildStoreItemRecycler(query);
        adapter.updateOptions(options);
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        loadSavedCurrency(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_user_store_currency:
                showCurrencyDialog(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortMenuDialog() {
        new SortDialog().show(getSupportFragmentManager(), "SortDialog");
    }

    private void loadSavedCurrency(Menu menu) {
        if (sp.contains(MyConstants.CURRENCY_NAME)) {
            MenuItem item = menu.findItem(R.id.menu_user_store_currency);
            item.setTitle(sp.getString(MyConstants.CURRENCY_NAME, null));
        }
    }

    private void showCurrencyDialog(MenuItem item) {
        CurrencyDialog dialog = new CurrencyDialog();
        dialog.setDialogCallback(new DialogCallback() {
            @Override
            public void onDialogActionFinished(String result) {
                changeActiveCurrency(item, result);
            }
        });
        dialog.show(getSupportFragmentManager(), "CurrencyDialog");
    }

    private void changeActiveCurrency(MenuItem item, String result) {
        item.setTitle(result);

        SharedPreferences.Editor editor = sp.edit();

        activeCurrencyValue = currencies.get(result);
        editor.putString(MyConstants.CURRENCY_VALUE, activeCurrencyValue.toString());

        activeCurrencyName = result;
        editor.putString(MyConstants.CURRENCY_NAME, activeCurrencyName).apply();

        setUpRecyclerView();
    }

    private void logout() {
        new LogoutDialog().show(getSupportFragmentManager(), "LogoutDialog");
    }
}
