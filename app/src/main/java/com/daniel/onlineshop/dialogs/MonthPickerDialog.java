package com.daniel.onlineshop.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

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

public class MonthPickerDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_month);
        builder.setMessage(R.string.dialog_select_month);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchBySelectedMonth(getStringMonth(numberPicker));
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(numberPicker);
        return builder.create();
    }

    private String getStringMonth(NumberPicker numberPicker) {
        String month;
        if (numberPicker.getValue() < 10) { month = "0" + numberPicker.getValue(); }
        else { month = String.valueOf(numberPicker.getValue()); }
        return month;
    }

    private void searchBySelectedMonth(String month) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference statisticRef = db.collection(MyConstants.COLLECTION_STATISTIC_NAME);
        StatisticActivity activity = (StatisticActivity) getActivity();
        Query query = statisticRef
                .whereEqualTo(MyConstants.STATISTIC_KEY_MONTH, month)
                .orderBy(MyConstants.STATISTIC_ORDER_BY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<StatisticItem> options = new FirestoreRecyclerOptions.Builder<StatisticItem>()
                .setQuery(query, StatisticItem.class)
                .build();
        activity.getAdapter().updateOptions(options);
    }
}
