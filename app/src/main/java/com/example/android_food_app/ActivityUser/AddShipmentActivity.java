package com.example.android_food_app.ActivityUser;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_food_app.Model.Customer;
import com.example.android_food_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddShipmentActivity extends AppCompatActivity {
    private EditText edit_email, edit_id, edit_name, edit_phone, edit_address;
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_shipment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edit_email = findViewById(R.id.edit_email);
        edit_id = findViewById(R.id.edit_id);
        edit_name = findViewById(R.id.edit_name);
        edit_phone = findViewById(R.id.edit_phone);
        edit_address = findViewById(R.id.edit_address);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            edit_email.setText(email); // Hiển thị email của người dùng hiện tại trong trường edit_email
        } else {
            Toast.makeText(AddShipmentActivity.this, "Không thể lấy thông tin người dùng hiện tại.", Toast.LENGTH_SHORT).show();
        }


        btn_add = findViewById(R.id.btn_add);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin người dùng hiện tại từ Firebase Authentication
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String email = currentUser.getEmail();
                    edit_email.setText(email);
                    Long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
                    edit_id.setText(String.valueOf(id));
                    String name = edit_name.getText().toString();
                    String phone = edit_phone.getText().toString();
                    String address = edit_address.getText().toString();
                    Customer customer = new Customer(email, id, name, phone, address);
                    onClickAddCustomer(customer);
                } else {
                    Toast.makeText(AddShipmentActivity.this, "Không thể lấy thông tin người dùng hiện tại.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void onClickAddCustomer(Customer customer) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("customers");

        // Đẩy dữ liệu của khách hàng lên Firebase Realtime Database
        String pathObject = String.valueOf(customer.getId());
        myRef.child(pathObject).setValue(customer, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(AddShipmentActivity.this, "Add Customer Success!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}