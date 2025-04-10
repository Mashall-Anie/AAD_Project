package com.example.android_food_app.ActivityAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.AdapterAdmin.FoodAdminAdapter;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodPageAdminActivity extends AppCompatActivity {
    private RecyclerView rcv_food;
    private List<Product> mListProduct;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private FloatingActionButton fab_add;
    private ImageButton imgBack;
    private SearchView search_view;
    private FoodAdminAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_page_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // anh xa
        rcv_food = findViewById(R.id.rcv_food);
        fab_add = findViewById(R.id.fab_add);
        imgBack = findViewById(R.id.imgBack);
        search_view = findViewById(R.id.search_view);
        search_view.clearFocus();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodPageAdminActivity.this, HomeAdminActivity.class);
                startActivity(intent);
            }
        });

        // Ấn icon add để vào trang Thêm
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodPageAdminActivity.this, AddFoodAdminActivity.class);
                startActivity(intent);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(FoodPageAdminActivity.this, 1);
        rcv_food.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(FoodPageAdminActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_admin_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        mListProduct = new ArrayList<>();
        adapter = new FoodAdminAdapter(FoodPageAdminActivity.this, mListProduct);
        rcv_food.setAdapter(adapter);

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        Query query = productsRef.orderByChild("productType").equalTo("Món ngon");
        dialog.show();

        // load data
        eventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListProduct.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    Product product = itemSnapshot.getValue(Product.class);
                    product.setKey(itemSnapshot.getKey());
                    mListProduct.add(product);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

    }

    public void searchList(String text) {
        ArrayList<Product> searchList = new ArrayList<>();
        for (Product product: mListProduct) {
            if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(product);
            }
        }

        adapter.searchDataList(searchList);

    }


}