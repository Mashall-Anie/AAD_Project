package com.example.android_food_app.AdapterAdmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.Model.Order;
import com.example.android_food_app.Model.OrderDetail;
import com.example.android_food_app.Model.User;
import com.example.android_food_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class OrderAdminAdapter extends RecyclerView.Adapter<OrderAdminAdapter.OrderViewHolder> {
    // Khai báo biến DatabaseReference để truy cập Firebase
    private DatabaseReference ordersRef;
    private Context mContext;
    private List<Order> mListOrder;

    public OrderAdminAdapter(Context mContext) {
        this.mContext = mContext;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders"); // Khởi tạo DatabaseReference
    }

    public void setData(List<Order> list) {
        this.mListOrder = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderAdminAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_admin, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdminAdapter.OrderViewHolder holder, int position) {
        Order order = mListOrder.get(position);
        if (order == null) {
            return;
        }

        holder.txt_id_order.setText(order.getId());
        holder.txt_name.setText(order.getName());
        holder.txt_phone_order.setText(order.getPhone());
        holder.txt_address_order.setText(order.getAddress());
        holder.txt_date_order.setText(order.getDate());
        holder.txt_total_order.setText(formatPrice(order.getTotal()));


        // Hiển thị danh sách sản phẩm
//        StringBuilder productsText = new StringBuilder();
//        for (OrderDetail detail : order.getOrderDetails()) {
//            productsText.append(detail.getProductName()).append(", ");
//            // Nếu bạn muốn hiển thị số lượng và tổng giá tiền của từng sản phẩm, bạn có thể bổ sung vào đây.
//        }
//        // Xóa dấu ',' ở cuối
//        if (productsText.length() > 2) {
//            productsText.delete(productsText.length() - 2, productsText.length());
//        }
//        holder.txt_name_order.setText(productsText.toString());

        // Set trạng thái cho checkbox dựa vào status của đơn hàng
        holder.chk_accept.setChecked("Xác nhận".equals(order.getStatus()));
        holder.chk_accept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                order.setStatus("Xác nhận");
            } else {
                order.setStatus("Chưa xác nhận");
            }

            // Cập nhật status lên Firebase
            ordersRef.child(order.getId()).child("status").setValue(order.getStatus());
        });

    }

    @Override
    public int getItemCount() {
        if (mListOrder != null) {
            return mListOrder.size();
        }
        return 0;
    }

    // anh xa id
    public class  OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_id_order, txt_name, txt_phone_order, txt_address_order, txt_name_order, txt_date_order, txt_total_order;
        private CheckBox chk_accept;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_id_order = itemView.findViewById(R.id.txt_id_order);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_phone_order = itemView.findViewById(R.id.txt_phone_order);
            txt_address_order = itemView.findViewById(R.id.txt_address_order);
            txt_name_order = itemView.findViewById(R.id.txt_name_order);
            txt_date_order = itemView.findViewById(R.id.txt_date_order);
            txt_total_order = itemView.findViewById(R.id.txt_total_order);

            chk_accept = itemView.findViewById(R.id.chk_accept);
        }

    }

    // Thêm phương thức định dạng giá
    private String formatPrice(double price) {
        if (price == 0) {
            return "0";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' '); // Sử dụng khoảng trắng làm dấu phân cách nhóm số
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(price);
    }
}
