package com.example.android_food_app.ActivityUser;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.AdapterUser.OrderHistoryAdapter;
import com.example.android_food_app.Model.Customer;
import com.example.android_food_app.Model.Order;
import com.example.android_food_app.Model.OrderDetail;
import com.example.android_food_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrderHistory;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<Order> orderList;
    private ImageButton imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewOrderHistory = findViewById(R.id.rcv_order_history);
        orderList = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(orderList, new OrderHistoryAdapter.IClickListener() {
            @Override
            public void onClickUpdateItem(Order order) {

            }

            @Override
            public void onClickDeleteItem(Order order) {

            }

            @Override
            public void onClick(Order order) {

            }
        });  // Đúng tên lớp

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewOrderHistory.setLayoutManager(layoutManager);
        recyclerViewOrderHistory.setAdapter(orderHistoryAdapter);

        imgBack = findViewById(R.id.imgBack);  // Sửa lỗi ID ở đây
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getListUserFromRealtimeDatabase();
    }

    private void getListUserFromRealtimeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Orders");

        // Lấy thông tin người dùng hiện tại từ Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserUid = currentUser.getUid();
            myRef.orderByChild("userID").equalTo(currentUserUid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try {
                        Order order = snapshot.getValue(Order.class);
                        if (order != null) {
                            orderList.add(order);
                            orderHistoryAdapter.notifyDataSetChanged();
                        }
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //khi chỉnh sửa 1 item
                    Order order = snapshot.getValue(Order.class);
                    if (order == null || orderList == null || orderList.isEmpty()) {
                        return;
                    }
                    for(int i = 0; i< orderList.size(); i++) {
                        if(Objects.equals(order.getId(), orderList.get(i).getId())){
                            orderList.set(i, order);
                            break;
                        }
                    }
                    orderHistoryAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    // khi xóa 1 item
                    Order order = snapshot.getValue(Order.class);
                    if (order == null || orderList == null || orderList.isEmpty()) {
                        return;
                    }

                    // Xóa khách hàng khỏi danh sách
                    for (int i = 0; i < orderList.size(); i++) {
                        if (Objects.equals(order.getId(), orderList.get(i).getId())) {
                            orderList.remove(i);
                            break;
                        }
                    }

                    // Cập nhật RecyclerView
                    orderHistoryAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }



}
