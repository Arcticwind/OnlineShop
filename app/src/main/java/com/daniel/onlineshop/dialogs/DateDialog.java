package com.daniel.onlineshop.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.daniel.onlineshop.activities.StatisticActivity;
import com.daniel.onlineshop.models.StatisticItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.TimeZone;

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        formatDateIntoString(year, month+1, day);
    }

    private void formatDateIntoString(int year, int month, int day) {
        String sMonth, sDay;
        String sYear = String.valueOf(year);

        if (month < 10) { sMonth = "0" + month; }
        else { sMonth = String.valueOf(month); }

        if (day < 10) { sDay = "0" + day; }
        else { sDay = String.valueOf(day); }

        Toast.makeText(getActivity(), sYear + "-" + sMonth + "-" + sDay, Toast.LENGTH_SHORT).show();
        showEntriesByExactDate(sYear, sMonth, sDay);
    }

    private void showEntriesByExactDate(String year, String month, String day) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference statisticRef = db.collection(MyConstants.COLLECTION_STATISTIC_NAME);
        StatisticActivity activity = (StatisticActivity) getActivity();
        Query query = statisticRef
                .whereEqualTo(MyConstants.STATISTIC_KEY_YEAR, year)
                .whereEqualTo(MyConstants.STATISTIC_KEY_MONTH, month)
                .whereEqualTo(MyConstants.STATISTIC_KEY_DAY, day)
                .orderBy(MyConstants.STATISTIC_ORDER_BY, Query.Direction.DESCENDING);
                query.get();

        FirestoreRecyclerOptions<StatisticItem> options = new FirestoreRecyclerOptions.Builder<StatisticItem>()
                .setQuery(query, StatisticItem.class)
                .build();
        activity.getAdapter().updateOptions(options);
    }
}
