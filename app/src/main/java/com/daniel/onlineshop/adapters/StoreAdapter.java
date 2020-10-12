package com.daniel.onlineshop.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daniel.onlineshop.R;
import com.daniel.onlineshop.models.StoreItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class StoreAdapter extends FirestoreRecyclerAdapter<StoreItem, StoreAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private Context context;

    public void deleteItem(int position) { getSnapshots().getSnapshot(position).getReference().delete(); }

    public StoreAdapter(@NonNull FirestoreRecyclerOptions<StoreItem> options, Context context) {
        super(options);
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription, tvPrice, tvQuantity;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_user_store_title);
            tvDescription = itemView.findViewById(R.id.tv_user_store_availability);
            tvPrice = itemView.findViewById(R.id.tv_user_store_price);
            tvQuantity = itemView.findViewById(R.id.tv_user_store_num_available);
            imageView = itemView.findViewById(R.id.iv_store_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_store_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull StoreItem model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvDescription.setText(model.getDescription());
        holder.tvPrice.setText(String.valueOf(model.getPrice()));
        holder.tvQuantity.setText(String.valueOf(model.getQuantity()));
        Glide.with(context).load(model.getImageUrl()).into(holder.imageView);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
