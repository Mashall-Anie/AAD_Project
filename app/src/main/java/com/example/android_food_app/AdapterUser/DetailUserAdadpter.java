package com.example.android_food_app.AdapterUser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;

import java.util.List;

public class DetailUserAdadpter extends RecyclerView.Adapter<DetailUserAdadpter.DetailViewHolder>{
    private List<Product> listProductOther;

    public void setData(List<Product> listProductOther) {
        this.listProductOther = listProductOther;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_product_user, parent,false);
        return new DetailUserAdadpter.DetailViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(listProductOther != null) {
            return listProductOther.size();
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        Product imgother  = listProductOther.get(position);
        if(imgother == null) {
            return;
        }
        // Load hình ảnh của sản phẩm vào ImageView bằng Glide
        if (imgother.getImgURLOther() != null && !imgother.getImgURLOther().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imgother.getImgURLOther())
                    .into(holder.img_other);
        } else {
            holder.img_other.setVisibility(View.GONE); // Ẩn ImageView nếu không có hình ảnh
        }


    }

    public class DetailViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_other;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            img_other = itemView.findViewById(R.id.img_other);

        }
    }
}
