package com.example.android_food_app.AdapterUser;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_food_app.ActivityUser.DetailPageUserActivity;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;

import java.util.List;

public class DrinkUserRecycleViewAdapter extends RecyclerView.Adapter<DrinkUserRecycleViewAdapter.DrinkViewHolder> {
    private List<Product> list_drink;
    private Context mContext;

    public DrinkUserRecycleViewAdapter(List<Product> list_drink, Context mContext) {
        this.list_drink = list_drink;
        this.mContext = mContext;
    }

    public void setDataDrink(List<Product> list_drink) {
        this.list_drink = list_drink;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_user, parent,false);
        return new DrinkUserRecycleViewAdapter.DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Product product = list_drink.get(position);
        if(product == null) {
            return;
        }
        holder.txt_name.setText(product.getName());
        holder.txt_price_old.setText(product.getPriceOld() + " VNĐ");
        holder.txt_price_new.setText(product.getPriceNew() + " VNĐ");

        // Kiểm tra và hiển thị phần giảm giá khi có
        if (product.getSale() != null && !product.getSale().isEmpty()) {
            holder.txt_sale.setText("Giảm " + product.getSale() +"%");
            holder.txt_sale.setVisibility(View.VISIBLE);
        } else {
            holder.txt_sale.setVisibility(View.GONE);
        }

        // Kiểm tra và hiển thị phần giảm giá và giá cũ khi có
        if (product.getPriceOld() != null && !product.getPriceOld().isEmpty() && !product.getPriceOld().equals("0")) {
            holder.txt_price_old.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
        } else {
            holder.txt_price_old.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
        }

        // Load hình ảnh từ URL vào ImageView bằng Glide
        if (product.getImgURL() != null && !product.getImgURL().isEmpty()) {
            Glide.with(mContext)
                    .load(product.getImgURL())
                    .into(holder.imgUrl);
        } else {
            // Nếu không có URL hình ảnh, có thể ẩn hoặc đặt ảnh mặc định cho ImageView
            holder.imgUrl.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTrangchu = new Intent(mContext, DetailPageUserActivity.class);
                intentTrangchu.putExtra("dessert_recycleview", product); //đảm bảo dữ liệu của sp có thể được truyền vào intent phải khai báo Serializable
                mContext.startActivity(intentTrangchu);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list_drink != null) {
            return list_drink.size();
        }
        return 0;
    }

    public class DrinkViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgUrl;
        private TextView txt_sale;
        private TextView txt_name;
        private TextView txt_price_old;
        private TextView txt_price_new;

        private View line;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUrl = itemView.findViewById(R.id.img_food);
            txt_sale = itemView.findViewById(R.id.txt_sale);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_price_old = itemView.findViewById(R.id.txt_price_old);
            txt_price_new = itemView.findViewById(R.id.txt_price_new);
            line = itemView.findViewById(R.id.line);

        }
    }

}
