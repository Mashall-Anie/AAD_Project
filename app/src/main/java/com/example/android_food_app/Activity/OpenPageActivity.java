package com.example.android_food_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_food_app.ActivityAdmin.HomeAdminActivity;
import com.example.android_food_app.ActivityUser.HomeUserActivity;
import com.example.android_food_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OpenPageActivity extends AppCompatActivity {
    private static final String TAG = "OpenPageActivity";
    Button loginButton;
    TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton = findViewById(R.id.loginButton);
        textViewRegister = findViewById(R.id.textViewRegister);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Người dùng đã đăng nhập, lấy vai trò của họ và chuyển hướng
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Long role = snapshot.child("role").getValue(Long.class);
                        if (role != null && role == 0) { // role == 0 là admin
                            Intent intent = new Intent(OpenPageActivity.this, HomeAdminActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpenPageActivity.this, HomeUserActivity.class);
                            startActivity(intent);
                        }
                        finish(); // Kết thúc hoạt động hiện tại để nó không có trong ngăn xếp
                    } else {
                        Log.d(TAG, "Không có dữ liệu người dùng");
                        // Chuyển đến màn hình đăng nhập nếu không có dữ liệu người dùng
                        redirectToLogin();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "Lỗi khi lấy dữ liệu người dùng", error.toException());
                    // Chuyển đến màn hình đăng nhập nếu việc truy vấn bị hủy
                    redirectToLogin();
                }
            });
        } else {
            // Người dùng chưa đăng nhập, hiển thị tùy chọn đăng nhập/đăng ký sau một khoảng thời gian
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    redirectToLogin();
                }
            }, 2000);

            loginButton.setOnClickListener(view -> {
                Intent intent = new Intent(OpenPageActivity.this, LoginActivity.class);
                startActivity(intent);
            });

            textViewRegister.setOnClickListener(view -> {
                Intent intent = new Intent(OpenPageActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(OpenPageActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Kết thúc hoạt động hiện tại để nó không có trong ngăn xếp
    }
}
