package com.example.android_food_app.ActivityAdmin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;

public class UpdateDrinkAdminActivity extends AppCompatActivity {
    private EditText update_edt_name, update_edt_desc, update_edt_price, update_edt_sale;
    private ImageView update_imgUrl, update_imgSlider, update_imgOther;
    private RadioButton update_rad_popular1, update_rad_popular2, update_rad_product_type1, update_rad_product_type_drink, update_rad_product_type_dessert;
    private RadioGroup update_radioGroup_product_type;
    private Button btn_update;
    private ImageButton imgBack;
    private String name, desc, price, sale;
    private String imgUrl, imgSlider, imgOther;
    private String key, oldImgUrl, oldImgSlider, oldImgOther;
    private Uri uriImgUrl, uriImgSlider, uriImgOther;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_drink_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        update_edt_name = findViewById(R.id.update_edt_name);
        update_edt_desc = findViewById(R.id.update_edt_desc);
        update_edt_price = findViewById(R.id.update_edt_price);
        update_edt_sale = findViewById(R.id.update_edt_sale);
        update_imgUrl = findViewById(R.id.update_imgUrl);
        update_imgSlider = findViewById(R.id.update_imgSlider);
        update_imgOther = findViewById(R.id.update_imgOther);
        update_rad_popular1 = findViewById(R.id.update_rad_popular1);
        update_rad_popular2 = findViewById(R.id.update_rad_popular2);
        update_rad_product_type1 = findViewById(R.id.update_rad_product_type1);
        update_rad_product_type_drink = findViewById(R.id.update_rad_product_type_drink);
        update_rad_product_type_dessert = findViewById(R.id.update_rad_product_type_dessert);
        update_radioGroup_product_type = findViewById(R.id.update_radioGroup_product_type);
        imgBack = findViewById(R.id.imgBack);
        btn_update = findViewById(R.id.btn_update);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ActivityResultLauncher Ảnh chính
        ActivityResultLauncher<Intent> activityResultLauncher1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uriImgUrl = data.getData();
                            update_imgUrl.setImageURI(uriImgUrl);
                        } else {
                            Toast.makeText(UpdateDrinkAdminActivity.this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // ActivityResultLauncher Ảnh Slider
        ActivityResultLauncher<Intent> activityResultLauncher2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uriImgSlider = data.getData();
                            update_imgSlider.setImageURI(uriImgSlider);
                        } else {
                            Toast.makeText(UpdateDrinkAdminActivity.this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // ActivityResultLauncher Ảnh Other
        ActivityResultLauncher<Intent> activityResultLauncher3 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uriImgOther = data.getData();
                            update_imgOther.setImageURI(uriImgOther);
                        } else {
                            Toast.makeText(UpdateDrinkAdminActivity.this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Nhận sản phẩm từ Detail gửi về
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(UpdateDrinkAdminActivity.this).load(bundle.getString("imgUrl")).into(update_imgUrl);
            Glide.with(UpdateDrinkAdminActivity.this).load(bundle.getString("imgSlider")).into(update_imgSlider);
            Glide.with(UpdateDrinkAdminActivity.this).load(bundle.getString("imgOther")).into(update_imgOther);
            update_edt_name.setText(bundle.getString("name"));
            update_edt_desc.setText(bundle.getString("desc"));
            update_edt_price.setText(bundle.getString("price"));
            update_edt_sale.setText(bundle.getString("sale"));
            // Nhận chuỗi "popular" và thiết lập RadioButton
            String popularStatus = bundle.getString("popular");
            if (popularStatus != null) {
                if (popularStatus.equals("Có")) {
                    update_rad_popular1.setChecked(true);
                } else if (popularStatus.equals("Không")) {
                    update_rad_popular2.setChecked(true);
                }
            }
            String productType = bundle.getString("productType");
            if (productType != null) {
                switch (productType) {
                    case "Món ngon":
                        update_rad_product_type1.setChecked(true);
                        break;
                    case "Đồ uống":
                        update_rad_product_type_drink.setChecked(true);
                        break;
                    case "Tráng miệng":
                        update_rad_product_type_dessert.setChecked(true);
                        break;
                    default:
                        // Xử lý trường hợp không tìm thấy productType nào phù hợp
                        break;
                }
            }

            key = bundle.getString("Key");
            oldImgUrl = bundle.getString("imgUrl");
            oldImgSlider = bundle.getString("imgOther");
            oldImgOther = bundle.getString("imgSlider");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        update_imgUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher1.launch(photoPicker);
            }
        });

        update_imgSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher2.launch(photoPicker);
            }
        });

        update_imgOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher3.launch(photoPicker);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMainImage();
                Intent intent = new Intent(UpdateDrinkAdminActivity.this, DrinkPageAdminActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateMainImage() {
        if (uriImgUrl == null) {
            updateSliderImage();
        } else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Save Images")
                    .child(uriImgUrl.getLastPathSegment());

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateDrinkAdminActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_admin_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            storageReference.putFile(uriImgUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUrl = uri.toString();
                            updateSliderImage();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(UpdateDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateSliderImage() {
        if (uriImgSlider == null) {
            updateOtherImage();
        } else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Save Images")
                    .child(uriImgSlider.getLastPathSegment());

            storageReference.putFile(uriImgSlider).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgSlider = uri.toString();
                            updateOtherImage();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateOtherImage() {
        if (uriImgOther == null) {
            updateData();
        } else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Save Images")
                    .child(uriImgOther.getLastPathSegment());

            storageReference.putFile(uriImgOther).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgOther = uri.toString();
                            updateData();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateData() {
        name = update_edt_name.getText().toString().trim();
        desc = update_edt_desc.getText().toString().trim();
        price = update_edt_price.getText().toString().trim();
        sale = update_edt_sale.getText().toString().trim();
        boolean isPopular = update_rad_popular1.isChecked();

        if (uriImgUrl == null) {
            imgUrl = oldImgUrl;
        }
        if (uriImgSlider == null) {
            imgSlider = oldImgSlider;
        }
        if (uriImgOther == null) {
            imgOther = oldImgOther;
        }

        String productType = "";
        int checkedRadioButtonId = update_radioGroup_product_type.getCheckedRadioButtonId();

        if (checkedRadioButtonId == R.id.update_rad_product_type1) {
            productType = "Món ngon";
        } else if (checkedRadioButtonId == R.id.update_rad_product_type_drink) {
            productType = "Đồ uống";
        } else if (checkedRadioButtonId == R.id.update_rad_product_type_dessert) {
            productType = "Tráng miệng";
        }

        // Loại bỏ tất cả khoảng trắng từ chuỗi giá
        price = price.replaceAll("\\s+", "");
        sale = sale.replaceAll("\\s+", "");

        // Format giá
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        double priceValue = Double.parseDouble(price.replace(",", "").replace(".", ""));
        price = decimalFormat.format(priceValue);

        // Tính giá mới sau khuyến mãi
        double saleValue;
        if (!sale.isEmpty()) {
            saleValue = Double.parseDouble(sale.replace(",", "").replace(".", ""));
        } else {
            saleValue = 0;
        }
        double newPriceValue = priceValue - (priceValue * (saleValue / 100));
        String priceNewFormatted = decimalFormat.format(newPriceValue);

        // Tạo đối tượng Product với thông tin mới
//        Product product = new Product(name, desc, price, priceNewFormatted, sale, imgUrl, imgSlider, isPopular, productType, imgOther);

        // Tạo đối tượng Product mới với thông tin cập nhật
        Product product = new Product();
        product.setName(name);
        product.setDesc(desc);
        product.setPriceOld(price);
        product.setPriceNew(priceNewFormatted);
        product.setSale(sale);
        product.setImgURL(imgUrl);
        product.setImgURlSlider(imgSlider);
        product.setPopular(isPopular);
        product.setProductType(productType);
        product.setImgURLOther(imgOther);

        databaseReference.child(key).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(new Intent(UpdateDrinkAdminActivity.this, DrinkPageAdminActivity.class));
                Toast.makeText(UpdateDrinkAdminActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
