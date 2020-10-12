package com.daniel.onlineshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.models.HistoryItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HistoryAdapter extends FirestoreRecyclerAdapter<HistoryItem, HistoryAdapter.ViewHolder> {

    public HistoryAdapter(@NonNull FirestoreRecyclerOptions<HistoryItem> options) {
        super(options);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvEmail, tvOperation, tvDate, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_history_title);
            tvEmail = itemView.findViewById(R.id.tv_history_email);
            tvOperation = itemView.findViewById(R.id.tv_history_operation);
            tvDate = itemView.findViewById(R.id.tv_history_date);
            tvPrice = itemView.findViewById(R.id.tv_history_price);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_history_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HistoryItem model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvEmail.setText(model.getEmail());
        holder.tvOperation.setText(model.getOperation());
        holder.tvDate.setText(model.getDate());
        holder.tvPrice.setText(String.valueOf(model.getPrice()));
    }
}
