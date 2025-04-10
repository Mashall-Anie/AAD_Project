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

public class FoodUserRecycleViewAdapter extends RecyclerView.Adapter<FoodUserRecycleViewAdapter.FoodViewHolder> {
    private List<Product> list_food;
    private Context activity;


    public FoodUserRecycleViewAdapter(List<Product> list_food, Context mContext) {
        this.list_food = list_food;
        this.activity = mContext;
    }

    public void setDataFood(List<Product> list_food) {
        this.list_food = list_food;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_user, parent,false);
        return new FoodUserRecycleViewAdapter.FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Product product = list_food.get(position);
        if(product == null) {
            return;
        }
        holder.txt_name.setText(product.getName());
        holder.txt_price_old.setText(product.getPriceOld() + " VNĐ");
        holder.txt_price_new.setText(product.getPriceNew() + " VNĐ");
        if(product.getDesc() != null && !product.getDesc().isEmpty()) {
            holder.txt_desc.setText(product.getDesc());
            holder.txt_desc.setVisibility(View.GONE);
        } else {
            holder.txt_desc.setText(product.getDesc());
            holder.txt_desc.setVisibility(View.GONE);
        }

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
            Glide.with(activity)
                    .load(product.getImgURL())
                    .into(holder.imgUrl);
        } else {
            // Nếu không có URL hình ảnh, có thể ẩn hoặc đặt ảnh mặc định cho ImageView
            holder.imgUrl.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTrangchu = new Intent(activity, DetailPageUserActivity.class);
                intentTrangchu.putExtra("food_recycleview", product); //đảm bảo dữ liệu của sp có thể được truyền vào intent phải khai báo Serializable
                activity.startActivity(intentTrangchu);
            }
        });



    }

    @Override
    public int getItemCount() {
        if(list_food != null) {
            return list_food.size();
        }
        return 0;
    }

    public  class FoodViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgUrl;
        private TextView txt_sale;
        private TextView txt_name;
        private TextView txt_price_old;
        private TextView txt_price_new;
        private TextView txt_desc;

        private View line;
        public FoodViewHolder(@NonNull View itemView) {

            super(itemView);
            imgUrl = itemView.findViewById(R.id.img_food);
            txt_sale = itemView.findViewById(R.id.txt_sale);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_price_old = itemView.findViewById(R.id.txt_price_old);
            txt_price_new = itemView.findViewById(R.id.txt_price_new);
            txt_desc = itemView.findViewById(R.id.txt_desc);
            line = itemView.findViewById(R.id.line);

        }
    }

}
