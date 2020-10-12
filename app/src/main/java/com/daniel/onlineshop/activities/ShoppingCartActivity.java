package com.daniel.onlineshop.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.adapters.OnItemClickListener;
import com.daniel.onlineshop.adapters.ShoppingCartAdapter;
import com.daniel.onlineshop.models.ReceiptItem;
import com.daniel.onlineshop.models.ShoppingCartItem;
import com.daniel.onlineshop.models.SoldItem;
import com.daniel.onlineshop.models.StoreItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyDateFormats;
import com.daniel.onlineshop.utils.MyMethods;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShoppingCartActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_shopping_cart)
    Toolbar toolbar;

    @BindView(R.id.rv_shopping_cart)
    RecyclerView recyclerView;

    @BindView(R.id.tv_shopping_cart_total_value)
    TextView tvTotalValue;

    @BindView(R.id.tv_shopping_cart_currency_name)
    TextView tvCurrencyName;

    @OnClick(R.id.btn_payment_options)
    public void btnPaymentOptions() {
        toPaymentOptions();
    }


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ShoppingCartAdapter adapter;
    private CollectionReference userShoppingCartRef, receiptRef, storeItemRef, soldItemsRef;
    private MyMethods mm = new MyMethods();
    private MyDateFormats df = new MyDateFormats();
    private SharedPreferences sp;
    private FirestoreRecyclerOptions<ShoppingCartItem> options;
    private String paymentAmount;

    private PayPalPayment payment;
    private int itemsBought = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        userShoppingCartRef = db.collection(MyConstants.COLLECTION_SHOPPING_CART).document(mm.getFirebaseUserEmail()).collection(MyConstants.SUB_COLLECTION_USER_CART);
        Query query = userShoppingCartRef.orderBy(MyConstants.SHOPPING_CART_ORDER_BY, Query.Direction.DESCENDING);

        buildShoppingCartRecycler(query);

        adapter = new ShoppingCartAdapter(options, getApplicationContext(), MyConstants.BASE_CURRENCY_VALUE, MyConstants.BASE_CURRENCY_NAME);

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
                updateTotalPrice();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot ds) {
                toItemDetails(ds);
            }
        });
    }

    private void updateTotalPrice() {
        userShoppingCartRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                double itemPrice = 0;
                for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                    ShoppingCartItem sci = ds.toObject(ShoppingCartItem.class);
                    itemPrice += sci.getItemsWanted() * sci.getStoreItem().getPrice();
                }
                setTotalPriceText(itemPrice);
            }
        });
    }

    private void buildShoppingCartRecycler(Query query) {
        options = new FirestoreRecyclerOptions.Builder<ShoppingCartItem>()
                .setQuery(query, ShoppingCartItem.class)
                .build();
    }

    private void toItemDetails(DocumentSnapshot ds) {
        ShoppingCartItem sci = ds.toObject(ShoppingCartItem.class);
        Intent intent = new Intent(getApplicationContext(), ItemDetailsActivity.class);
        intent.putExtra(MyConstants.EXTRA_ID, sci.getStoreItem().getTitle());
        intent.putExtra(MyConstants.EXTRA_QUANTITY, sci.getItemsWanted());
        startActivity(intent);
    }

    private void setTotalPriceText(double itemPrice) {
        String total = String.valueOf(mm.roundDecNumber(itemPrice, 2));
        tvTotalValue.setText(total);
        tvCurrencyName.setText(MyConstants.BASE_CURRENCY_NAME);
    }

    private void toPaymentOptions() {
        PayPalConfiguration config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .merchantName(MyConstants.PAYPAL_MERCHANT_NAME)
                .clientId(MyConstants.PAYPAL_CLIENT_ID);

        Intent paypalIntent = new Intent(this, PayPalService.class);
        paypalIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(paypalIntent);

        paymentAmount = tvTotalValue.getText().toString();
        payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), MyConstants.BASE_CURRENCY_NAME, getString(R.string.paypal_total), PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, MyConstants.PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyConstants.PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null) {
                    createReceipt(confirm);
                    updateStorageQuantity();
                    createSoldItemEntry();
                    clearShoppingCartItems();
                    startActivity(new Intent(getApplicationContext(), UserStoreActivity.class));

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, getString(R.string.toast_order_cancelled), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, getString(R.string.toast_order_invalid), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createSoldItemEntry() {
        userShoppingCartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult() != null) {
                    for (DocumentSnapshot ds : task.getResult()) {
                        ShoppingCartItem sci = ds.toObject(ShoppingCartItem.class);

                        soldItemsRef = db.collection(MyConstants.COLLECTION_SOLD_ITEMS);
                        String itemCost = String.valueOf(mm.roundDecNumber(sci.getItemsWanted() * sci.getStoreItem().getPrice(), 2));
                        SoldItem si = new SoldItem(mm.getFirebaseUserEmail(), sci.getItemsWanted(), sci.getStoreItem().getTitle(), itemCost);
                        soldItemsRef.add(si);
                    }
                }
            }
        });
    }

    private void updateStorageQuantity() {
        userShoppingCartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    getItemsWanted(task);
                }
            }
        });
    }

    private void getItemsWanted(Task<QuerySnapshot> task) {
        for (DocumentSnapshot ds : task.getResult()) {
            ShoppingCartItem sci = ds.toObject(ShoppingCartItem.class);
            itemsBought = sci.getItemsWanted();
            decreaseQuantity(sci.getStoreItem().getTitle());
        }
    }

    private void decreaseQuantity(String title) {
        storeItemRef = db.collection(MyConstants.COLLECTION_ITEM_NAME);
        storeItemRef.document(title).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    setNewStoreItemQuantity(task, title);
                }
            }
        });
    }

    private void setNewStoreItemQuantity(Task<DocumentSnapshot> task, String title) {
        DocumentSnapshot ds = task.getResult();
        StoreItem si = ds.toObject(StoreItem.class);
        si.setQuantity(si.getQuantity() - itemsBought);
        storeItemRef.document(title).set(si);
    }

    private void createReceipt(PaymentConfirmation confirm) {
        String paymentDetails = null;
        try {
            paymentDetails = confirm.toJSONObject().toString(4);
            JSONObject jsonObject = new JSONObject(paymentDetails);
            String id = jsonObject.getJSONObject("response").getString("id");
            ReceiptItem receiptItem = new ReceiptItem(df.getDateTime(), id, paymentAmount);

            receiptRef = db.collection(MyConstants.COLLECTION_RECEIPT).document(mm.getFirebaseUserEmail()).collection(MyConstants.SUB_COLLECTION_RECEIPT);
            receiptRef.document(id).set(receiptItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearShoppingCartItems() {
        userShoppingCartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot ds : task.getResult()) {
                        userShoppingCartRef.document(ds.getId()).delete();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        updateTotalPrice();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping_cart, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
