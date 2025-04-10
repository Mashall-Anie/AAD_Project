package com.example.android_food_app.ActivityUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.AdapterUser.OrderUserAdapter;
import com.example.android_food_app.Model.Customer;
import com.example.android_food_app.Model.Order;
import com.example.android_food_app.Model.OrderDetail;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CreateOrderActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_CUSTOMER = 1;
    TextView txtTotalAmount;
    private RecyclerView rcvCreateOrder;
    private OrderUserAdapter orderUserAdapter;
    private Button btnCreateOrder, btn_customer;
    private EditText edt_name_order, edt_phone_order, edt_address_order;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        rcvCreateOrder = findViewById(R.id.rcv_create_order);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        btn_customer = findViewById(R.id.btn_customer);
        edt_name_order = findViewById(R.id.edt_name_order);
        edt_phone_order = findViewById(R.id.edt_phone_order);
        edt_address_order = findViewById(R.id.edt_address_order);
        // Get the total amount from the Intent
        double totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.0) * 1000; // Nhân cho 1000 để có 3 số 0 ở cuối
        String formattedTotalAmount = String.format(Locale.getDefault(), "%.3f VND", totalAmount);
        txtTotalAmount.setText(formattedTotalAmount);

        rcvCreateOrder.setLayoutManager(new LinearLayoutManager(this));
        orderUserAdapter = new OrderUserAdapter(this);
        rcvCreateOrder.setAdapter(orderUserAdapter);

        btn_customer.setOnClickListener(v -> {
            Intent intent = new Intent(CreateOrderActivity.this, ShipmentDetailPageActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SELECT_CUSTOMER);
        });
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        btnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy thông tin từ các ô nhập liệu
                String name = edt_name_order.getText().toString().trim();
                String phone = edt_phone_order.getText().toString().trim();
                String address = edt_address_order.getText().toString().trim();
                double total = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.0);
                double deliveryCost = 20000; // Chi phí giao hàng mặc định
                String currentDate = getCurrentDateTime(); // Lấy ngày giờ hiện tại

                // Lấy userID của tài khoản đang đăng nhập
                String userID = getCurrentUserID();

                // Tạo ID ngẫu nhiên cho đơn hàng
                String orderId = generateRandomOrderId();

                // Trạng thái mặc định của đơn hàng
                String status = "Chưa xác nhận";

                // Tạo đối tượng Order mới
                Order newOrder = new Order(orderId, userID, name, phone, address, total, deliveryCost, currentDate, status);

                // Lưu đơn hàng vào Realtime Database (ví dụ Firebase)
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
                ordersRef.child(orderId).setValue(newOrder)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Đã lưu thành công đơn hàng
                                Toast.makeText(CreateOrderActivity.this, "Đơn hàng đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                                // Có thể thực hiện các hành động tiếp theo như chuyển màn hình hoặc cập nhật giao diện
                                saveOrderDetails(orderId);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Đã xảy ra lỗi trong quá trình lưu đơn hàng
                                Toast.makeText(CreateOrderActivity.this, "Đã xảy ra lỗi khi tạo đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                // Có thể hiển thị thông báo lỗi chi tiết hoặc thực hiện xử lý khác
                            }
                        });
            }
        });
        img_back = findViewById(R.id.imgBack);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Method to get current date and time
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Method to get current user ID
    private String getCurrentUserID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            // Handle the case where user is not authenticated or null
            return ""; // Or throw an exception, based on your app's logic
        }
    }

    // Method to generate a random order ID
    private String generateRandomOrderId() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomOrderId = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            randomOrderId.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
        return randomOrderId.toString();
    }
    private void saveOrderDetails(String orderId) {
        DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance().getReference().child("OrderDetails");
        List<Product> productList = orderUserAdapter.getProductList(); // Get products from adapter

        for (Product product : productList) {
            int quantity = orderUserAdapter.getProductQuantity(product); // Get quantity from adapter
            double price = Double.parseDouble(product.getPriceNew());
            double totalPrice = price * quantity;

            // Create new OrderDetail object
            OrderDetail orderDetail = new OrderDetail(orderId, product.getName(), quantity, totalPrice);

            // Save order detail to Firebase
            String orderDetailId = orderDetailsRef.push().getKey(); // Generate unique key for OrderDetail
            orderDetailsRef.child(orderDetailId).setValue(orderDetail)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // OrderDetail saved successfully
                            // You may add further logic here if needed

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle errors here
                            Toast.makeText(CreateOrderActivity.this, "Failed to save order detail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_CUSTOMER && resultCode == RESULT_OK && data != null) {
            Customer selectedCustomer = (Customer) data.getSerializableExtra("selected_customer");
            if (selectedCustomer != null) {
                edt_name_order.setText(selectedCustomer.getName());
                edt_phone_order.setText(selectedCustomer.getPhone());
                edt_address_order.setText(selectedCustomer.getAddress());
            }
        }
    }
}
