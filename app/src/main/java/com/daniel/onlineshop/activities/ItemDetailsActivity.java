package com.daniel.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.daniel.onlineshop.R;
import com.daniel.onlineshop.models.ShoppingCartItem;
import com.daniel.onlineshop.models.StoreItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ItemDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_item_details)
    Toolbar toolbar;

    @BindView(R.id.tv_item_details_title)
    TextView tvTitle;

    @BindView(R.id.tv_item_details_description)
    TextView tvDescription;

    @BindView(R.id.tv_item_details_price)
    TextView tvPrice;

    @BindView(R.id.tv_item_details_currency_name)
    TextView tvCurrencyName;

    @BindView(R.id.tv_item_details_items_remaining)
    TextView tvRemaining;

    @BindView(R.id.tv_item_details_currently_in_cart)
    TextView tvCurrentlyInCart;

    @BindView(R.id.et_item_details_order_quantity)
    EditText etQuantity;

    @BindView(R.id.iv_item_details)
    ImageView imageView;

    @OnClick(R.id.btn_item_details_plus)
    public void btnPlus() {
        increaseItemNumber();
    }

    @OnClick(R.id.btn_item_details_minus)
    public void btnMinus() {
        decreaseItemNumber();
    }

    @OnClick(R.id.btn_item_details_add_to_cart)
    public void btnAddToCart() { addToCart(); }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemRef = db.collection(MyConstants.COLLECTION_ITEM_NAME);
    private CollectionReference shopCartRef = db.collection(MyConstants.COLLECTION_SHOPPING_CART);
    private MyMethods mm = new MyMethods();
    private double itemPrice = 0.0;
    private int itemsRemaining = 0, itemsWanted = 0;
    private StoreItem item;
    private double coef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        ButterKnife.bind(this);

        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            coef = getIntent().getDoubleExtra(MyConstants.CURRENCY_VALUE, 1);

        }
        getFirebaseData();
    }

    private void getFirebaseData() {
        String id = getIntent().getExtras().getString(MyConstants.EXTRA_ID);
        tvTitle.setText(id);

        itemRef.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {
                setFirebaseData(ds);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ItemDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        shopCartRef.document(mm.getFirebaseUserEmail()).collection(MyConstants.SUB_COLLECTION_USER_CART).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult() != null && task.getResult().exists()) {
                    ShoppingCartItem sci = task.getResult().toObject(ShoppingCartItem.class);
                    itemsWanted = Objects.requireNonNull(sci).getItemsWanted();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ItemDetailsActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFirebaseData(DocumentSnapshot ds) {
        item = ds.toObject(StoreItem.class);
        itemsRemaining = item.getQuantity();
        itemPrice =  item.getPrice() * coef;

        tvTitle.setText(item.getTitle());
        tvDescription.setText(item.getDescription());
        tvPrice.setText(String.valueOf(mm.roundDecNumber(itemPrice, 2)));
        tvCurrencyName.setText(getIntent().getStringExtra(MyConstants.CURRENCY_NAME));
        tvRemaining.setText(String.valueOf(itemsRemaining));
        tvCurrentlyInCart.setText(String.valueOf(itemsWanted));
        etQuantity.setText(String.valueOf(itemsWanted));
        Glide.with(this).load(item.getImageUrl()).into(imageView);
    }

    private void increaseItemNumber() {
        int itemNumber = Integer.parseInt(etQuantity.getText().toString());
        if (itemNumber < itemsRemaining) {
            itemNumber += 1;
            etQuantity.setText(String.valueOf(itemNumber));
        }

        if (itemNumber > itemsRemaining) {
            itemNumber = itemsRemaining;
            etQuantity.setText(String.valueOf(itemNumber));
        }
    }

    private void decreaseItemNumber() {
        int itemNumber = Integer.parseInt(etQuantity.getText().toString());
        if (itemNumber >= 1) {
            itemNumber -= 1;
            etQuantity.setText(String.valueOf(itemNumber));
        }

        if (itemNumber > itemsRemaining) {
            itemNumber = itemsRemaining;
            etQuantity.setText(String.valueOf(itemNumber));
        }
    }

    private void addToCart() {
        int itemsWanted = Integer.parseInt(etQuantity.getText().toString());
        if (itemsWanted > 0 && itemsWanted <= itemsRemaining) {
            addItemsToCart(itemsWanted);
        }

        if (itemsWanted == 0) {
            removeItemFromCart();
        }
    }

    private void removeItemFromCart() {
        shopCartRef.document(mm.getFirebaseUserEmail()).collection(MyConstants.SUB_COLLECTION_USER_CART).document(item.getTitle()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ItemDetailsActivity.this, getString(R.string.toast_item_removed_cart), Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ItemDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemsToCart(int itemsWanted) {
        ShoppingCartItem sci = new ShoppingCartItem();
        sci.setItemsWanted(itemsWanted);
        sci.setStoreItem(item);
        shopCartRef.document(mm.getFirebaseUserEmail()).collection(MyConstants.SUB_COLLECTION_USER_CART).document(item.getTitle()).set(sci).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_items_added), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), UserStoreActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
