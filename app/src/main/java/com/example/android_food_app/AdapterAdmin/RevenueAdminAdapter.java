package com.example.android_food_app.AdapterAdmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.Model.Order;
import com.example.android_food_app.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class RevenueAdminAdapter extends RecyclerView.Adapter<RevenueAdminAdapter.RevenueViewHolder>{
    private Context mContext;
    private List<Order> mListOrder;

    public RevenueAdminAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Order> list) {
        this.mListOrder = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RevenueAdminAdapter.RevenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revenue_admin, parent, false);
        return new RevenueAdminAdapter.RevenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueAdminAdapter.RevenueViewHolder holder, int position) {
        Order order = mListOrder.get(position);
        if (order == null) {
            return;
        }

        holder.txt_id.setText(order.getId());
        holder.txt_date.setText(order.getDate());
        holder.txt_total.setText(formatPrice(order.getTotal()));
        holder.line.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        if (mListOrder != null) {
            return mListOrder.size();
        }
        return 0;
    }

    public class RevenueViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_id, txt_date, txt_total;
        private View line;

        public RevenueViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_id = itemView.findViewById(R.id.txt_id);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_total = itemView.findViewById(R.id.txt_total);
            line = itemView.findViewById(R.id.line);

        }
    }

    // Thêm phương thức định dạng giá
    private String formatPrice(double price) {
        if (price == 0) {
            return "0";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' '); // Sử dụng khoảng trắng làm dấu phân cách nhóm số
        DecimalFormat decimalFormat = new DecimalFormat("#,###.000", symbols);
        return decimalFormat.format(price);
    }

}
