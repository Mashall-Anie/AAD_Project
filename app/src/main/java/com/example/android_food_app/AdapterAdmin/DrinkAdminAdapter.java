package com.example.android_food_app.AdapterAdmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_food_app.ActivityAdmin.DetailProductDrinkAdminActivity;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DrinkAdminAdapter extends RecyclerView.Adapter<DrinkAdminAdapter.DrinkViewHolder> {
    private Context mContext;
    private List<Product> mListProduct;
    private FirebaseStorage firebaseStorage;

    public DrinkAdminAdapter(Context mContext, List<Product> mListProduct) {
        this.mContext = mContext;
        this.mListProduct = mListProduct;
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_admin, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Product product = mListProduct.get(position);
        if (product == null) {
            return;
        }

        // Hiển thị 1 số thuộc tính sản phẩm lên trang danh sách
        Glide.with(mContext).load(mListProduct.get(position).getImgURL()).into(holder.imgUrl);
        holder.txt_name.setText(mListProduct.get(position).getName());
        holder.txt_desc.setText(mListProduct.get(position).getDesc());
        holder.txt_sale.setText(mListProduct.get(position).getSale());
        holder.txt_price_old.setText(mListProduct.get(position).getPriceOld());
        holder.txt_popular.setText(mListProduct.get(position).getPopular() ? "Có" : "Không");
        holder.txt_product_type.setText(mListProduct.get(position).getProductType());


        // Tính giá mới priceNew sau khi nhập khuyến mãi sale
        try {
            String priceOldStr = product.getPriceOld().replaceAll("[^\\d]", ""); // Xóa các ký tự không phải là số
            double priceOld = Double.parseDouble(priceOldStr);

            // Kiểm tra xem có giảm giá hay không
            if (!product.getSale().isEmpty()) {
                int salePercent = Integer.parseInt(product.getSale().replaceAll("[^\\d]", ""));
                double priceNew = priceOld * (100 - salePercent) / 100;

                DecimalFormat formatter = new DecimalFormat("###,###");
                String priceNewFormatted = formatter.format(priceNew);
                holder.txt_price_new.setText(priceNewFormatted);
                holder.line.setVisibility(View.VISIBLE);
            } else {
                holder.txt_price_new.setVisibility(View.GONE);
                holder.line.setVisibility(View.GONE);
                holder.txt_sale.setVisibility(View.GONE);
                holder.txt_sale0.setVisibility(View.GONE);
                holder.txt_sale1.setVisibility(View.GONE);
                holder.txt_title_price_new.setVisibility(View.GONE);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Ấn vào 1 layout item gửi sp sang trang DetailProductAdmin
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Product product = mListProduct.get(position);

                Intent intent = new Intent(mContext, DetailProductDrinkAdminActivity.class);
                intent.putExtra("imgUrl", product.getImgURL());
                intent.putExtra("imgOther", product.getImgURLOther());
                intent.putExtra("imgSlider", product.getImgURlSlider());
                intent.putExtra("name", product.getName());
                intent.putExtra("desc", product.getDesc());
                intent.putExtra("price", product.getPriceOld());
                intent.putExtra("sale", product.getSale());
                intent.putExtra("popular", product.getPopular());
                intent.putExtra("productType", product.getProductType());
                intent.putExtra("Key", product.getKey());

                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        if (mListProduct != null) {
            return mListProduct.size();
        }
        return 0;
    }

    public void searchDataList(ArrayList<Product> searchList) {
        mListProduct = searchList;
        notifyDataSetChanged();
    }

    public class DrinkViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_name, txt_desc, txt_sale, txt_price_old, txt_price_new, txt_popular, txt_product_type, txt_sale0, txt_sale1, txt_title_price_new;
        private CardView recCard;
        private ImageView imgUrl, imgSlider, imgOther;
        private View line;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_name = itemView.findViewById(R.id.txt_name);
            txt_desc = itemView.findViewById(R.id.txt_desc);
            txt_price_old = itemView.findViewById(R.id.txt_price_old);
            txt_sale = itemView.findViewById(R.id.txt_sale);
            txt_price_new = itemView.findViewById(R.id.txt_price_new);
            txt_popular = itemView.findViewById(R.id.txt_popular);
            txt_product_type = itemView.findViewById(R.id.txt_product_type);
            imgUrl = itemView.findViewById(R.id.imgUrl);
            imgSlider = itemView.findViewById(R.id.imgSlider);
            imgOther = itemView.findViewById(R.id.imgOther);

            recCard = itemView.findViewById(R.id.recCard);

            line = itemView.findViewById(R.id.line);
            txt_sale0 = itemView.findViewById(R.id.txt_sale0);
            txt_sale1 = itemView.findViewById(R.id.txt_sale1);
            txt_title_price_new = itemView.findViewById(R.id.txt_title_price_new);
        }
    }


}
