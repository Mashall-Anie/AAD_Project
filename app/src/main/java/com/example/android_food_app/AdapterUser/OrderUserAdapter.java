package com.example.android_food_app.AdapterUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.Model.CartManager;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;

import java.util.List;

public class OrderUserAdapter extends RecyclerView.Adapter<OrderUserAdapter.OrderViewHolder> {
    private Context context;
    private List<Product> productList;

    public OrderUserAdapter(Context context) {
        this.context = context;
        this.productList = CartManager.getInstance().getCartProducts();
    }
    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged(); // Thông báo cho adapter biết rằng dữ liệu đã thay đổi
    }
    public int getProductQuantity(Product product) {
        // Tìm và trả về số lượng của sản phẩm trong giỏ hàng
        int quantity = CartManager.getInstance().getProductQuantity(product);
        return quantity; // Trả về 0 nếu không tìm thấy sản phẩm
    }
    public List<Product> getProductList() {
        return productList;
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_create_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductQuantity;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
        }

        public void bind(Product product) {
            txtProductName.setText(product.getName());
            int quantity = CartManager.getInstance().getProductQuantity(product);
            txtProductQuantity.setText("Số lượng: " + quantity);
        }
    }
}
