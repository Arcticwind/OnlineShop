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
import com.daniel.onlineshop.models.ShoppingCartItem;
import com.daniel.onlineshop.utils.MyMethods;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ShoppingCartAdapter extends FirestoreRecyclerAdapter<ShoppingCartItem, ShoppingCartAdapter.ViewHolder> {

    private Double currencyRate;
    private String currencyName;
    private Context context;
    private MyMethods mm = new MyMethods();
    private OnItemClickListener listener;

    public void deleteItem(int position) { getSnapshots().getSnapshot(position).getReference().delete(); }

    public ShoppingCartAdapter(@NonNull FirestoreRecyclerOptions<ShoppingCartItem> options, Context context, Double currencyRate, String currencyName) {
        super(options);
        this.context = context;
        this.currencyRate = currencyRate;
        this.currencyName = currencyName;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvPrice, tvQuantity, tvTotal, tvCurrencyName;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_rv_shopping_title);
            tvPrice = itemView.findViewById(R.id.tv_rv_shopping_price);
            tvQuantity = itemView.findViewById(R.id.tv_rv_shopping_quantity);
            tvTotal = itemView.findViewById(R.id.tv_rv_shopping_total);
            tvCurrencyName = itemView.findViewById(R.id.tv_shopping_cart_currency_name);
            imageView = itemView.findViewById(R.id.iv_shopping_cart_image);

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_shopping_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ShoppingCartItem model) {
        Double total = model.getItemsWanted() * model.getStoreItem().getPrice();
        holder.tvTitle.setText(model.getStoreItem().getTitle());
        holder.tvPrice.setText(String.valueOf(mm.roundDecNumber(model.getStoreItem().getPrice() * currencyRate, 2)));
        holder.tvQuantity.setText(String.valueOf(model.getItemsWanted()));
        holder.tvTotal.setText(String.valueOf(mm.roundDecNumber(total * currencyRate, 2)));
        holder.tvCurrencyName.setText(currencyName);
        Glide.with(context).load(model.getStoreItem().getImageUrl()).into(holder.imageView);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
