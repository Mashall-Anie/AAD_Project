package com.example.android_food_app.ActivityAdmin;

import static android.app.PendingIntent.getActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.AdapterAdmin.RevenueAdminAdapter;
import com.example.android_food_app.Model.Order;
import com.example.android_food_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RevenuePageAdminActivity extends AppCompatActivity {
    private EditText edt_from_date, edt_to_date;
    private ImageButton imgBack;
    private RecyclerView rcv_revenue;
    private RevenueAdminAdapter revenueAdminAdapter;
    private TextView totalRevenue;
    private List<Order> allOrders;
    private ProgressDialog progressDialog;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_page_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang kiểm tra...");

        // Ánh xạ các view từ layout
        edt_from_date = findViewById(R.id.edt_from_date);
        edt_to_date = findViewById(R.id.edt_to_date);
        imgBack = findViewById(R.id.imgBack);
        totalRevenue = findViewById(R.id.totalRevenue);
        rcv_revenue = findViewById(R.id.rcv_revenue);

        // Thiết lập sự kiện click cho các EditText và ImageButton
        edt_from_date.setOnClickListener(v -> showDatePickerDialog(edt_from_date));
        edt_to_date.setOnClickListener(v -> showDatePickerDialog(edt_to_date));
        imgBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView và Adapter
        revenueAdminAdapter = new RevenueAdminAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_revenue.setLayoutManager(linearLayoutManager);
        rcv_revenue.setAdapter(revenueAdminAdapter);

        // Khởi tạo Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("Orders");

        // Lấy danh sách đơn hàng từ Firebase và hiển thị lên RecyclerView
        fetchOrdersFromFirebase();
    }

    private void fetchOrdersFromFirebase() {
        progressDialog.show();
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allOrders = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        allOrders.add(order);
                    }
                }
                progressDialog.dismiss();
                // Sắp xếp các đơn hà   ng theo ngày giảm dần
                Collections.sort(allOrders, (o1, o2) -> {
                    try {
                        Date date1 = dateFormat.parse(o1.getDate());
                        Date date2 = dateFormat.parse(o2.getDate());
                        if (date1 != null && date2 != null) {
                            return date2.compareTo(date1);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });

                // Sau khi lấy dữ liệu từ Firebase, gọi hàm lọc để hiển thị theo khoảng ngày đã chọn
                filterOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi đọc dữ liệu từ Firebase: " + error.getMessage());
            }
        });
    }

    private void filterOrders() {
        String fromDateString = edt_from_date.getText().toString();
        String toDateString = edt_to_date.getText().toString();

        if (fromDateString.isEmpty() || toDateString.isEmpty()) {
            revenueAdminAdapter.setData(allOrders);
            updateTotalRevenue(allOrders);
            return;
        }

        try {
            Date fromDate = dateFormat.parse(fromDateString);
            Date toDate = dateFormat.parse(toDateString);

            List<Order> filteredList = new ArrayList<>();
            for (Order order : allOrders) {
                Date orderDate = dateFormat.parse(order.getDate());
                if (orderDate != null && !orderDate.before(fromDate) && !orderDate.after(toDate)) {
                    filteredList.add(order);
                }
            }

            revenueAdminAdapter.setData(filteredList);
            updateTotalRevenue(filteredList);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalRevenue(List<Order> list) {
        double total = 0;
        for (Order order : list) {
            total += order.getTotal();
        }
        totalRevenue.setText(formatPrice(total));
    }

    private String formatPrice(double price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' '); // Sử dụng khoảng trắng làm dấu phân cách nhóm số
        DecimalFormat decimalFormat = new DecimalFormat("#,###.000", symbols);
        return decimalFormat.format(price) + " VND";
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RevenuePageAdminActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                    String date = dateFormat.format(calendar.getTime());
                    editText.setText(date);
                    filterOrders(); // Gọi lại để lọc đơn hàng khi thay đổi ngày
                }, year, month, day);
        datePickerDialog.show();
    }
}
