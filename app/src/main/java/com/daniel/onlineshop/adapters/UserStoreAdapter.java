package com.daniel.onlineshop.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daniel.onlineshop.R;
import com.daniel.onlineshop.models.StoreItem;
import com.daniel.onlineshop.utils.MyMethods;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserStoreAdapter extends FirestoreRecyclerAdapter<StoreItem, UserStoreAdapter.ViewHolder> {

    private Double currencyRate;
    private String currencyName;
    private Context context;
    private MyMethods mm;
    private OnItemClickListener listener;

    public UserStoreAdapter(@NonNull FirestoreRecyclerOptions<StoreItem> options, Context context, Double currencyRate, String currencyName) {
        super(options);
        this.context = context;
        this.currencyRate = currencyRate;
        this.currencyName = currencyName;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvAvailability, tvPrice, tvCurrencyName;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_user_store_title);
            tvAvailability = itemView.findViewById(R.id.tv_user_store_availability);
            tvPrice = itemView.findViewById(R.id.tv_user_store_price);
            tvCurrencyName = itemView.findViewById(R.id.tv_user_store_currency_name);
            imageView = itemView.findViewById(R.id.iv_user_store);

            mm = new MyMethods();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_store, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull StoreItem model) {
        setAvailabilityColorText(holder, model);
        holder.tvTitle.setText(model.getTitle());
        holder.tvPrice.setText(String.valueOf(mm.roundDecNumber(model.getPrice() * currencyRate, 2)));
        holder.tvCurrencyName.setText(currencyName);
        Glide.with(context).load(model.getImageUrl()).into(holder.imageView);
    }

    private void setAvailabilityColorText(ViewHolder holder, StoreItem model) {
        int quantity = model.getQuantity();
        TextView tvAvailability = holder.tvAvailability;
        if (quantity <= 0) {
            tvAvailability.setText(R.string.rv_sold_out);
            tvAvailability.setTextColor(ContextCompat.getColor(context, R.color.colorSoldOut));
        }
        else if (quantity <= 3) {
            tvAvailability.setText(R.string.rv_almost_gone);
            tvAvailability.setTextColor(ContextCompat.getColor(context, R.color.colorAlmostGone));
        }
        else {
            tvAvailability.setText(R.string.rv_in_stock);
            tvAvailability.setTextColor(ContextCompat.getColor(context, R.color.colorInStock));
        }
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
