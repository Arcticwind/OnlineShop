package com.daniel.onlineshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.models.StatisticItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class StatisticAdapter extends FirestoreRecyclerAdapter<StatisticItem, StatisticAdapter.ViewHolder> {

    public StatisticAdapter(@NonNull FirestoreRecyclerOptions<StatisticItem> options) {
        super(options);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate, tvAdds, tvUpdates, tvDeletes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_statistic_date);
            tvAdds = itemView.findViewById(R.id.tv_statistic_add);
            tvUpdates = itemView.findViewById(R.id.tv_statistic_update);
            tvDeletes = itemView.findViewById(R.id.tv_statistic_delete);
        }
    }

    @NonNull
    @Override
    public StatisticAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_statistic_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull StatisticAdapter.ViewHolder holder, int position, @NonNull StatisticItem model) {
        String title = model.getYear() + "-" + model.getMonth() + "-" + model.getDay();
        holder.tvDate.setText(title);
        holder.tvAdds.setText(String.valueOf(model.getAddCount()));
        holder.tvUpdates.setText(String.valueOf(model.getUpdateCount()));
        holder.tvDeletes.setText(String.valueOf(model.getDeleteCount()));
    }
}
