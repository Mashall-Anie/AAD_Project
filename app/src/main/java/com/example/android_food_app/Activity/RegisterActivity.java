package com.example.android_food_app.Activity;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_food_app.ActivityUser.HomeUserActivity;
import com.example.android_food_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private Button registerButton;
    private TextView textViewLogin;
    private EditText inputEmail, inputPassword, inputPassword2;
    private ProgressDialog progressDialog;
    private Drawable checkDrawableGray, checkDrawableBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton = findViewById(R.id.registerButton);
        textViewLogin = findViewById(R.id.textViewLogin);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (inputPassword.getRight() - inputPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Xử lý khi nhấn vào biểu tượng mắt
                        if (inputPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            // Hiển thị mật khẩu
                            inputPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bị gạch ngang
                            inputPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        } else {
                            // Ẩn mật khẩu
                            inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bình thường
                            inputPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                        }
                        // Đảm bảo con trỏ vẫn ở cuối text
                        inputPassword.setSelection(inputPassword.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });
        inputPassword2 = findViewById(R.id.inputPassword2);
        inputPassword2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (inputPassword2.getRight() - inputPassword2.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Xử lý khi nhấn vào biểu tượng mắt
                        if (inputPassword2.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            // Hiển thị mật khẩu
                            inputPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bị gạch ngang
                            inputPassword2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        } else {
                            // Ẩn mật khẩu
                            inputPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Đổi biểu tượng mắt thành mắt bình thường
                            inputPassword2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                        }
                        // Đảm bảo con trỏ vẫn ở cuối text
                        inputPassword2.setSelection(inputPassword2.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký...");
        checkDrawableGray = ContextCompat.getDrawable(this, R.drawable.ic_check);
        checkDrawableBlue = ContextCompat.getDrawable(this, R.drawable.ic_check_blue);
        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    // Nếu email hợp lệ, chuyển drawable thành dấu tích màu xanh nước biển
                    inputEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, checkDrawableBlue, null);
                } else {
                    // Nếu email không hợp lệ, chuyển drawable thành dấu tích màu xám
                    inputEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, checkDrawableGray, null);
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDangKy();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onClickDangKy() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String password2 = inputPassword2.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
            Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(password2)) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không trùng khớp.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            saveUserInfo(user);
                        } else {
                            String errorMessage = "Đăng ký thất bại. Vui lòng thử lại.";
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthException) {
                                FirebaseAuthException authEx = (FirebaseAuthException) exception;
                                switch (authEx.getErrorCode()) {
                                    case "ERROR_INVALID_EMAIL":
                                        errorMessage = "Email không hợp lệ.";
                                        break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        errorMessage = "Email đã được sử dụng.";
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        errorMessage = "Mật khẩu yếu. Vui lòng sử dụng ít nhất 6 ký tự.";
                                        break;
                                    default:
                                        errorMessage = "Đăng ký thất bại. Vui lòng thử lại.";
                                        break;
                                }
                            }
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInfo(FirebaseUser user) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", user.getEmail());
        userInfo.put("role", 1); // Mặc định là user

        dbRef.child("users").child(user.getUid()).setValue(userInfo)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User profile created for " + user.getUid());
                    Intent intent = new Intent(RegisterActivity.this, HomeUserActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                });
    }
}
