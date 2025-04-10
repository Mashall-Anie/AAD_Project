package com.example.android_food_app.AdapterUser;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_food_app.Model.CartManager;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapterRecycleView extends RecyclerView.Adapter<CartAdapterRecycleView.CartViewHolder> {
    private List<Product> list;
    private OnQuantityChangeListener quantityChangeListener;

    public CartAdapterRecycleView(List<Product> listCart, OnQuantityChangeListener quantityChangeListener) {
        this.list = listCart;
        this.quantityChangeListener = quantityChangeListener;
    }

    public void setDataSanPham(List<Product> list) {
        if (list != null) {
            this.list = list;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_user, parent, false);
        return new CartAdapterRecycleView.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = list.get(position);
        if (product == null) {
            return;
        }

        holder.txt_name_cart.setText(product.getName());
        holder.txt_price_cart.setText(formatPrice(CartManager.getInstance().getLinePrice(product)));
        // Đặt giá ban đầu

        // Load ảnh sử dụng Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImgURL())
                .into(holder.img_cart);

        // Số lượng ban đầu
        // Lấy currentQuantity từ CartManager
        int currentQuantity = CartManager.getInstance().getProductQuantity(product);
        holder.txt_count.setText(String.valueOf(currentQuantity));

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.txt_count.getText().toString());
                int newQuantity = currentQuantity + 1;
                holder.txt_count.setText(String.valueOf(newQuantity));

                // Update CartManager with the new quantity
                CartManager.getInstance().addProduct(product);

                // Update price
                double totalPrice = CartManager.getInstance().getLinePrice(product);
                holder.txt_price_cart.setText(formatPrice(totalPrice));

                // Notify RecyclerView to update UI for this item
                notifyItemChanged(holder.getAdapterPosition());

                // Notify the listener about the quantity change
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged();
                }
            }
        });

        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.txt_count.getText().toString());
                if (currentQuantity > 1) { // Ensure quantity does not go below 1
                    int newQuantity = currentQuantity - 1;
                    holder.txt_count.setText(String.valueOf(newQuantity));

                    // Update CartManager with the new quantity
                    CartManager.getInstance().decreaseProductQuantity(product);

                    // Update price
                    double totalPrice = CartManager.getInstance().getLinePrice(product);
                    holder.txt_price_cart.setText(formatPrice(totalPrice));

                    // Notify RecyclerView to update UI for this item
                    notifyItemChanged(holder.getAdapterPosition());

                    // Notify the listener about the quantity change
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onQuantityChanged();
                    }
                }
            }
        });

        holder.btn_delete_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa sản phẩm khỏi CartManager
                CartManager.getInstance().removeProduct(product);
                // Xóa sản phẩm khỏi danh sách hiển thị
                list.remove(position);
                // Thông báo cho RecyclerView về sự thay đổi
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());

                // Notify the listener about the quantity change
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged();
                }
            }
        });

    }
    private String formatPrice(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.000");
        return decimalFormat.format(price) + " VND";
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_cart, btn_minus, btn_add;
        private TextView txt_name_cart, txt_price_cart, txt_count;
        private ImageButton btn_delete_cart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            img_cart = itemView.findViewById(R.id.img_cart);
            btn_minus = itemView.findViewById(R.id.btn_minus);
            btn_add = itemView.findViewById(R.id.btn_add);
            txt_name_cart = itemView.findViewById(R.id.txt_name_cart);
            txt_price_cart = itemView.findViewById(R.id.txt_price_cart);
            txt_count = itemView.findViewById(R.id.txt_count);
            btn_delete_cart = itemView.findViewById(R.id.btn_delete_cart);
        }
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }
}
