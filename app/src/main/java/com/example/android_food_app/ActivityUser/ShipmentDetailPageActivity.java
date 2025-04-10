package com.example.android_food_app.ActivityUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.AdapterUser.ShipAddressRecycleviewAdapter;
import com.example.android_food_app.Model.Customer;
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

public class ShipmentDetailPageActivity extends AppCompatActivity {
    private RecyclerView rcv_address;
    private Button btn_add_address;
    private ShipAddressRecycleviewAdapter adapter;
    private List<Customer> customerList;
    private ImageButton imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shipment_detail_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgBack = findViewById(R.id.imgBack);  // Sửa lỗi ID ở đây
        rcv_address = findViewById(R.id.rcv_address);
        btn_add_address = findViewById(R.id.btn_add_address);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv_address.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcv_address.addItemDecoration(dividerItemDecoration);

        customerList = new ArrayList<>();
        adapter = new ShipAddressRecycleviewAdapter( customerList, new ShipAddressRecycleviewAdapter.IClickListener() {
            @Override
            public void onClickUpdateItem(Customer customer) {
                //tương tác với Realtime database chỉnh sửa 1 item nào đấy
                openDialogUpdateItem(customer);

            }

            @Override
            public void onClickDeleteItem(Customer customer) {
                onClickDeleteData(customer);

            }

            @Override
            public void onClick(Customer customer) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_customer", customer);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });
        rcv_address.setAdapter(adapter);


        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShipmentDetailPageActivity.this, AddShipmentActivity.class);
                startActivity(intent);
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getListUserFromRealtimeDatabase();
    }

    private void onClickDeleteData(Customer customer) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Bạn có muốn xóa bản ghi này không?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //map
                        //xóa 1 item
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("customers");

                        myRef.child(String.valueOf(customer.getId())).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(ShipmentDetailPageActivity.this,"Xóa thành công!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void openDialogUpdateItem(Customer customer) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_customer);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText edt_update_name = dialog.findViewById(R.id.edt_update_name);
        EditText edt_update_phone = dialog.findViewById(R.id.edt_update_phone);
        EditText edt_update_address = dialog.findViewById(R.id.edt_update_address);

        Button btnUpdate = dialog.findViewById(R.id.btn_update);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        //set data
        edt_update_name.setText(customer.getName());
        edt_update_phone.setText(customer.getPhone());
        edt_update_address.setText(customer.getAddress());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("customers");

                String newName = edt_update_name.getText().toString().trim();
                String newPhone = edt_update_phone.getText().toString().trim();
                String newAddress = edt_update_address.getText().toString().trim();


                customer.setName(newName);
                customer.setPhone(newPhone);
                customer.setAddress(newAddress);
                myRef.child(String.valueOf(customer.getId())).updateChildren(customer.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(ShipmentDetailPageActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void getListUserFromRealtimeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("customers");

        // Lấy thông tin người dùng hiện tại từ Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentEmail = currentUser.getEmail();
            myRef.orderByChild("emailuser").equalTo(currentEmail).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try {
                        Customer customer = snapshot.getValue(Customer.class);
                        if (customer != null) {
                            customerList.add(customer);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //khi chỉnh sửa 1 item
                    Customer customer = snapshot.getValue(Customer.class);
                    if (customer == null || customerList == null || customerList.isEmpty()) {
                        return;
                    }
                    for(int i = 0; i< customerList.size(); i++) {
                        if(Objects.equals(customer.getId(), customerList.get(i).getId())){
                            customerList.set(i, customer);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    // khi xóa 1 item
                    Customer deletedCustomer = snapshot.getValue(Customer.class);
                    if (deletedCustomer == null || customerList == null || customerList.isEmpty()) {
                        return;
                    }

                    // Xóa khách hàng khỏi danh sách
                    for (int i = 0; i < customerList.size(); i++) {
                        if (Objects.equals(deletedCustomer.getId(), customerList.get(i).getId())) {
                            customerList.remove(i);
                            break;
                        }
                    }

                    // Cập nhật RecyclerView
                    adapter.notifyDataSetChanged();

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