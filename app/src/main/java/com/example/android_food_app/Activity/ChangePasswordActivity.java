package com.example.android_food_app.Activity;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_food_app.Fragment.FragmentBottomNavigationUser.AccountUserFragment;
import com.example.android_food_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText inputPasswordOld, inputPasswordNew, inputPasswordNew2;
    Button changePasswordButton;
    private ProgressDialog progressDialog;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputPasswordOld = findViewById(R.id.inputPasswordOld);
        inputPasswordOld.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (inputPasswordOld.getRight() - inputPasswordOld.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Xử lý khi nhấn vào biểu tượng mắt
                        if (inputPasswordOld.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            // Hiển thị mật khẩu
                            inputPasswordOld.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bị gạch ngang
                            inputPasswordOld.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        } else {
                            // Ẩn mật khẩu
                            inputPasswordOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bình thường
                            inputPasswordOld.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                        }
                        // Đảm bảo con trỏ vẫn ở cuối text
                        inputPasswordOld.setSelection(inputPasswordOld.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });
        inputPasswordNew = findViewById(R.id.inputPasswordNew);
        inputPasswordNew.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (inputPasswordNew.getRight() - inputPasswordNew.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Xử lý khi nhấn vào biểu tượng mắt
                        if (inputPasswordNew.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            // Hiển thị mật khẩu
                            inputPasswordNew.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bị gạch ngang
                            inputPasswordNew.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        } else {
                            // Ẩn mật khẩu
                            inputPasswordNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bình thường
                            inputPasswordNew.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                        }
                        // Đảm bảo con trỏ vẫn ở cuối text
                        inputPasswordNew.setSelection(inputPasswordNew.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });
        inputPasswordNew2 = findViewById(R.id.inputPasswordNew2);
        inputPasswordNew2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (inputPasswordNew2.getRight() - inputPasswordNew2.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Xử lý khi nhấn vào biểu tượng mắt
                        if (inputPasswordNew2.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            // Hiển thị mật khẩu
                            inputPasswordNew2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bị gạch ngang
                            inputPasswordNew2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        } else {
                            // Ẩn mật khẩu
                            inputPasswordNew2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bình thường
                            inputPasswordNew2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                        }
                        // Đảm bảo con trỏ vẫn ở cuối text
                        inputPasswordNew2.setSelection(inputPasswordNew2.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });
        changePasswordButton = findViewById(R.id.changePasswordButton);
        img_back = findViewById(R.id.imgBack);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đổi mật khẩu...");

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePassword();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onClickChangePassword(){
        String oldPassword = inputPasswordOld.getText().toString().trim();
        String newPassword = inputPasswordNew.getText().toString().trim();
        String confirmPassword = inputPasswordNew2.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty() || oldPassword.isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới không khớp.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();

                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thất bại.", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error: " + task.getException().getMessage());
                                        }
                                    }
                                });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không đúng.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + task.getException().getMessage());
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(ChangePasswordActivity.this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
        }
    }
}
