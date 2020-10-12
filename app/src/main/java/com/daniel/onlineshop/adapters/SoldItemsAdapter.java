package com.daniel.onlineshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.models.SoldItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SoldItemsAdapter extends FirestoreRecyclerAdapter<SoldItem, SoldItemsAdapter.ViewHolder> {

    public void deleteItem(int position) { getSnapshots().getSnapshot(position).getReference().delete(); }

    public SoldItemsAdapter(@NonNull FirestoreRecyclerOptions<SoldItem> options) {
        super(options);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvQuantity, tvItemName, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_sold_items_email);
            tvQuantity = itemView.findViewById(R.id.tv_sold_items_quantity);
            tvItemName = itemView.findViewById(R.id.tv_sold_items_name);
            tvTotal = itemView.findViewById(R.id.tv_sold_items_total);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_sold_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull SoldItem model) {
        holder.tvEmail.setText(model.getEmail());
        holder.tvQuantity.setText(String.valueOf(model.getQuantity()));
        holder.tvItemName.setText(model.getItemName());
        holder.tvTotal.setText(model.getPriceTotal());
    }


}
