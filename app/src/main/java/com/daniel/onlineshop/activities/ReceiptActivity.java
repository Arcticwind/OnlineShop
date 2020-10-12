package com.daniel.onlineshop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.Toast;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.adapters.OnItemClickListener;
import com.daniel.onlineshop.adapters.ReceiptAdapter;
import com.daniel.onlineshop.models.ReceiptItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyMethods;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReceiptActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_receipt)
    Toolbar toolbar;

    @BindView(R.id.rv_receipt)
    RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference receiptRef;
    private MyMethods mm = new MyMethods();
    private FirestoreRecyclerOptions<ReceiptItem> options;
    private ReceiptAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        receiptRef = db.collection(MyConstants.COLLECTION_RECEIPT).document(mm.getFirebaseUserEmail()).collection(MyConstants.SUB_COLLECTION_RECEIPT);
        Query query = receiptRef.orderBy(MyConstants.RECEIPT_ORDER_BY, Query.Direction.DESCENDING);
        buildReceiptRecycler(query);

        adapter = new ReceiptAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setListener2(new OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot ds) {
                copyPaymentIdToClipboard(ds);
            }
        });
    }

    private void copyPaymentIdToClipboard(DocumentSnapshot ds) {
        ReceiptItem ri = ds.toObject(ReceiptItem.class);
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", ri.getPaymentId());
        manager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(), getString(R.string.toast_id_copied_clipboard), Toast.LENGTH_SHORT).show();
    }

    private void buildReceiptRecycler(Query query) {
        options = new FirestoreRecyclerOptions.Builder<ReceiptItem>()
                .setQuery(query, ReceiptItem.class)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
