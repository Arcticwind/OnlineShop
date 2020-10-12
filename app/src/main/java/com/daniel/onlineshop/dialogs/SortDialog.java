package com.daniel.onlineshop.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.activities.UserStoreActivity;
import com.daniel.onlineshop.models.StoreItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SortDialog extends DialogFragment {

    private int selectedItemIndex;
    private String[] sortList;
    private DialogCallback dialogCallback;
    private CollectionReference itemRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortList = getResources().getStringArray(R.array.query_order_by);
        itemRef = db.collection(MyConstants.COLLECTION_ITEM_NAME);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_sort_title);
        builder.setSingleChoiceItems(sortList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItemIndex = which;
            }
        });

        builder.setPositiveButton(R.string.dialog_descending, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String order = sortList[selectedItemIndex];
                Query query = itemRef.orderBy(order, Query.Direction.DESCENDING);
                updateQuery(query);
            }
        });

        builder.setNegativeButton(R.string.dialog_ascending, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String order = sortList[selectedItemIndex];
                Query query = itemRef.orderBy(order, Query.Direction.ASCENDING);
                updateQuery(query);
            }
        });

        return builder.create();
    }

    private void updateQuery(Query query) {
        UserStoreActivity activity = (UserStoreActivity) getActivity();
        FirestoreRecyclerOptions<StoreItem> options = new FirestoreRecyclerOptions.Builder<StoreItem>()
                .setQuery(query, StoreItem.class)
                .build();
        activity.getAdapter().updateOptions(options);
    }
}
