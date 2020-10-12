package com.daniel.onlineshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.models.ReceiptItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ReceiptAdapter extends FirestoreRecyclerAdapter<ReceiptItem, ReceiptAdapter.ViewHolder> {

    private OnItemClickListener listener2;

    public ReceiptAdapter(@NonNull FirestoreRecyclerOptions<ReceiptItem> options) {
        super(options);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDatetime, tvPaymentId, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDatetime = itemView.findViewById(R.id.tv_receipt_datetime);
            tvPaymentId = itemView.findViewById(R.id.tv_receipt_payment_id);
            tvPrice = itemView.findViewById(R.id.tv_receipt_payment_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener2.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()));
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_receipt_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ReceiptItem model) {
        holder.tvDatetime.setText(model.getDatetime());
        holder.tvPaymentId.setText(model.getPaymentId());
        holder.tvPrice.setText(model.getTotalPrice());
    }

    public void setListener2(OnItemClickListener listener2) {
        this.listener2 = listener2;
    }
}
