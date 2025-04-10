package com.example.android_food_app.ActivityUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_food_app.AdapterUser.DetailUserAdadpter;
import com.example.android_food_app.Model.CartManager;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailPageUserActivity extends AppCompatActivity {
    private RecyclerView rcv_detail;
    private DetailUserAdadpter adapter;
    private ImageView img_back;
    private ImageView img_detail;
    private TextView txt_name_detail;
    private TextView txt_price_new_detail;
    private TextView txt_price_old_chitiet;
    private TextView txt_desc_detail;
    private View line;
    private Button btn_them;
    private Button btn_huybo;
    private Button btn_themvaogiohang;
    private ImageView img_chitiet_bottomsheet;
    private TextView txt_ten_chitiet_bottomsheet, txt_gia_chitiet_bottomsheet;
    private List<Product> productList = new ArrayList<>();
    private ImageView img_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_page_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rcv_detail = findViewById(R.id.rcv_detail);
        img_back = findViewById(R.id.img_back);
        img_detail = findViewById(R.id.img_detail);
        txt_name_detail = findViewById(R.id.txt_name_detail);
        txt_price_new_detail = findViewById(R.id.txt_price_new_detail);
        txt_price_old_chitiet = findViewById(R.id.txt_price_old_chitiet);
        txt_desc_detail = findViewById(R.id.txt_desc_detail);
        img_back = findViewById(R.id.img_back);
        line = findViewById(R.id.line);
        btn_them = findViewById(R.id.btn_them);
        img_cart = findViewById(R.id.img_cart);



        //khởi tạo adapter
        adapter = new DetailUserAdadpter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv_detail.setLayoutManager(linearLayoutManager);
        adapter.setData(productList);
        rcv_detail.setAdapter(adapter);
        rcv_detail.setNestedScrollingEnabled(false);
        rcv_detail.setFocusable(false);

        // Nhận Intent và lấy selectedProduct
        Intent intent = getIntent();
        Product selectedProduct = null;
        if (intent.hasExtra("drink_recycleview")) {
            selectedProduct = (Product) intent.getSerializableExtra("drink_recycleview");
        } else if (intent.hasExtra("food_recycleview")) {
            selectedProduct = (Product) intent.getSerializableExtra("food_recycleview");
        } else if (intent.hasExtra("dessert_recycleview")) {
            selectedProduct = (Product) intent.getSerializableExtra("dessert_recycleview");
        } else if (intent.hasExtra("product_detail")) {
            selectedProduct = (Product) intent.getSerializableExtra("product_detail");
        }

        // Fetch data từ Firebase dựa trên selectedProduct
        if (selectedProduct != null) {
            fetchDataFromFirebase(selectedProduct);
        }

        if (selectedProduct != null) {
            // Load hình ảnh từ URL vào ImageView bằng Glide
            if (selectedProduct.getImgURL() != null && !selectedProduct.getImgURL().isEmpty()) {
                Glide.with(this)
                        .load(selectedProduct.getImgURL())
                        .into(img_detail); // hoặc vào bất kỳ ImageView nào khác cần hiển thị hình ảnh
            } else {
                // Xử lý nếu không có URL hình ảnh
                img_detail.setVisibility(View.GONE);
            }
            txt_desc_detail.setText(selectedProduct.getDesc());
            txt_desc_detail.setVisibility(View.VISIBLE);
            txt_name_detail.setText(selectedProduct.getName());
            txt_price_new_detail.setText(selectedProduct.getPriceNew() + " VND");
            if (selectedProduct.getPriceOld() != null && !selectedProduct.getPriceOld().isEmpty()) {
                txt_price_old_chitiet.setText(selectedProduct.getPriceOld()+ " VND");
                txt_price_old_chitiet.setVisibility(View.VISIBLE);
            } else {
                txt_price_old_chitiet.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            }
        }
        // Lắng nghe khi click vào img_cart để chuyển đến giỏ hàng
        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPageUserActivity.this, HomeUserActivity.class);
                intent.putExtra("openCart", true);
                startActivity(intent);
            }
        });


        // Lắng nghe khi click vào button thêm vào giỏ hàng
        Product finalSelectedProduct = selectedProduct;
        btn_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenBottomSheetDialog(finalSelectedProduct);
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
    private void fetchDataFromFirebase(Product selectedProduct) {
        // Lắng nghe sự kiện khi có thay đổi dữ liệu trên Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("products");

        mDatabase.orderByChild("name").equalTo(selectedProduct.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear(); // Xóa dữ liệu cũ đi để tránh trường hợp dữ liệu bị trùng lặp
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy dữ liệu từ Firebase và thêm vào danh sách sản phẩm
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        // Kiểm tra nếu có imgURLOther thì thêm vào productList
                        if (product.getImgURLOther() != null && !product.getImgURLOther().isEmpty()) {
                            productList.add(product);
                        }
                    }
                }
                // Cập nhật dữ liệu mới vào adapter
                adapter.setData(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });
    }




    private void clickOpenBottomSheetDialog(Product product) {
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_dialog_chitiet, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailPageUserActivity.this);
        bottomSheetDialog.setContentView(viewBottomDialog);
        bottomSheetDialog.show();

        btn_huybo = viewBottomDialog.findViewById(R.id.btn_huybo);
        btn_themvaogiohang = viewBottomDialog.findViewById(R.id.btn_themvaogiohang);
        img_chitiet_bottomsheet = viewBottomDialog.findViewById(R.id.img_chitiet_bottomsheet);
        txt_gia_chitiet_bottomsheet = viewBottomDialog.findViewById(R.id.txt_gia_chitiet_bottomsheet);
        txt_ten_chitiet_bottomsheet = viewBottomDialog.findViewById(R.id.txt_ten_chitiet_bottomsheet);

        // Load hình ảnh từ URL vào ImageView bằng Glide (ví dụ)
        if (product.getImgURL() != null && !product.getImgURL().isEmpty()) {
            Glide.with(this)
                    .load(product.getImgURL())
                    .into(img_chitiet_bottomsheet); // Load vào ImageView của bottom sheet
        } else {
            img_chitiet_bottomsheet.setVisibility(View.GONE); // Xử lý nếu không có hình ảnh
        }

        img_chitiet_bottomsheet.setVisibility(View.VISIBLE);
        txt_gia_chitiet_bottomsheet.setText(product.getPriceNew() + " VND");
        txt_ten_chitiet_bottomsheet.setText(product.getName());


        btn_huybo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        btn_themvaogiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CartManager.getInstance().addProduct(product);
//                Toast.makeText(DetailPageUserActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
//
//                // Sau khi thêm sản phẩm vào giỏ hàng, chuyển đến HomeUserActivity và mở fragment giỏ hàng
//                Intent intent = new Intent(DetailPageUserActivity.this, HomeUserActivity.class);
//                intent.putExtra("openCart", true);
//                startActivity(intent);
//                finish(); // Đóng activity chi tiết nếu không cần thiết
//
//                bottomSheetDialog.dismiss();

                CartManager.getInstance().addProduct(product);
                Toast.makeText(DetailPageUserActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

    }
}