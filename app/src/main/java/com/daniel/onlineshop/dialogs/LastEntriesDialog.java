package com.daniel.onlineshop.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.activities.StatisticActivity;
import com.daniel.onlineshop.models.StatisticItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LastEntriesDialog extends DialogFragment {

    @BindView(R.id.et_dialog_number)
    EditText etLimit;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_show_recent_entries);
        builder.setMessage(R.string.dialog_last_entries);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.dialog_insert_last_entries, null);
        builder.setView(v);

        ButterKnife.bind(this, v);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long limit = Long.parseLong(etLimit.getText().toString());
                showMostRecentEntries(limit);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    private void showMostRecentEntries(long maxShownEntries) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference statisticRef = db.collection(MyConstants.COLLECTION_STATISTIC_NAME);
        StatisticActivity activity = (StatisticActivity) getActivity();
        Query query = statisticRef
                .orderBy(MyConstants.STATISTIC_ORDER_BY, Query.Direction.DESCENDING)
                .limit(maxShownEntries);
        FirestoreRecyclerOptions<StatisticItem> options = new FirestoreRecyclerOptions.Builder<StatisticItem>()
                .setQuery(query, StatisticItem.class)
                .build();
        activity.getAdapter().updateOptions(options);
    }
}
