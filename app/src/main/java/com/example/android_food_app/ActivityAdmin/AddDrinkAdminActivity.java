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

import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;

public class AddDrinkAdminActivity extends AppCompatActivity {
    private EditText edt_name, edt_desc, edt_price, edt_sale;
    private ImageView imgUrl, imgSlider, imgOther;
    private Uri uriMainImage, uriSliderImage, uriOtherImage;
    private String imgMainURL, imgSliderURL, imgOtherURL;
    private RadioButton rad_popular1, rad_popular2;
    private RadioGroup radioGroup_product_type;
    private Button btn_add;
    private ImageButton imgBack;
    private DatabaseReference currentIdReference;
    private long currentProductId;
    private static int currentId = 1; // Biến static để lưu trữ ID hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_drink_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // anh xa
        edt_name = findViewById(R.id.edt_name);
        edt_desc = findViewById(R.id.edt_desc);
        edt_price = findViewById(R.id.edt_price);
        edt_sale = findViewById(R.id.edt_sale);
        imgUrl = findViewById(R.id.imgUrl);
        imgSlider = findViewById(R.id.imgSlider);
        imgOther = findViewById(R.id.imgOther);
        rad_popular1 = findViewById(R.id.rad_popular1);
        rad_popular2 = findViewById(R.id.rad_popular2);
        btn_add = findViewById(R.id.btn_add);
        imgBack = findViewById(R.id.imgBack);
        radioGroup_product_type = findViewById(R.id.radioGroup_product_type);

        // tạo 1 node để luu trữ giá trị id
        currentIdReference = FirebaseDatabase.getInstance().getReference("currentProductId");
        // Lấy giá trị currentProductId hiện tại
        currentIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentProductId = snapshot.getValue(Long.class);
                } else {
                    currentProductId = 0; // Mặc định là 0 nếu chưa có sản phẩm nào
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddDrinkAdminActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // ActivityResultLauncher cho ảnh chính
        ActivityResultLauncher<Intent> mainImageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uriMainImage = data.getData();
                            imgUrl.setImageURI(uriMainImage);
                        } else {
                            Toast.makeText(AddDrinkAdminActivity.this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // ActivityResultLauncher cho ảnh slider
        ActivityResultLauncher<Intent> sliderImageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uriSliderImage = data.getData();
                            imgSlider.setImageURI(uriSliderImage);
                        } else {
                            Toast.makeText(AddDrinkAdminActivity.this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // ActivityResultLauncher cho ảnh khác
        ActivityResultLauncher<Intent> otherImageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uriOtherImage = data.getData();
                            imgOther.setImageURI(uriOtherImage);
                        } else {
                            Toast.makeText(AddDrinkAdminActivity.this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imgUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                mainImageResultLauncher.launch(photoPicker);
            }
        });

        imgSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                sliderImageResultLauncher.launch(photoPicker);
            }
        });

        imgOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                otherImageResultLauncher.launch(photoPicker);
            }
        });

        // add data
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMainImage();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Hàm upload ảnh chính lên Firebase
    private void saveMainImage() {
        if (uriMainImage == null) {
            Toast.makeText(AddDrinkAdminActivity.this, "Vui lòng chọn hình ảnh chính", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Save Images")
                .child(uriMainImage.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDrinkAdminActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_admin_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uriMainImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imgMainURL = urlImage.toString();
                saveSliderImage(dialog);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(AddDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm upload ảnh slider lên Firebase
    private void saveSliderImage(AlertDialog dialog) {
        if (uriSliderImage == null) {
            Toast.makeText(AddDrinkAdminActivity.this, "Vui lòng chọn hình ảnh slider", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Save Images")
                .child(uriSliderImage.getLastPathSegment());

        storageReference.putFile(uriSliderImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imgSliderURL = urlImage.toString();
                saveOtherImage(dialog);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(AddDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm upload ảnh khác lên Firebase
    private void saveOtherImage(AlertDialog dialog) {
        if (uriOtherImage == null) {
            Toast.makeText(AddDrinkAdminActivity.this, "Vui lòng chọn hình ảnh khác", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Save Images")
                .child(uriOtherImage.getLastPathSegment());

        storageReference.putFile(uriOtherImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imgOtherURL = urlImage.toString();
                uploadData(dialog);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(AddDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Hàm upload dữ liệu sản phẩm lên Firebase
    private void uploadData(AlertDialog dialog) {
        try {
            String name = edt_name.getText().toString().trim();
            String desc = edt_desc.getText().toString().trim();
            String price = edt_price.getText().toString().trim();
            String sale = edt_sale.getText().toString().trim();
            Boolean popular  = rad_popular1.isChecked(); // Lấy giá trị từ RadioButton

            // Kiểm tra các trường dữ liệu
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập tên sản phẩm");
            } else if (desc.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập mô tả sản phẩm");
            } else if (price.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập giá sản phẩm");
            }

            // Tính priceNew nếu có giảm giá
            double priceOld = Double.parseDouble(price.replaceAll("[^\\d]", ""));
            double priceNew = priceOld; // Giá mặc định sẽ là giá cũ
            if (!sale.isEmpty()) {
                int salePercent = Integer.parseInt(sale.replaceAll("[^\\d]", ""));
                priceNew = priceOld * (100 - salePercent) / 100;
            }
            // Format priceNew để lưu vào Firebase
            DecimalFormat formatter = new DecimalFormat("###,###");
            String priceNewFormatted = formatter.format(priceNew);

            // Lấy loại sản phẩm đã chọn từ RadioGroup
            int selectedProductTypeId = radioGroup_product_type.getCheckedRadioButtonId();
            RadioButton selectedProductTypeRadioButton = findViewById(selectedProductTypeId);
            String productType = selectedProductTypeRadioButton.getText().toString();

            // TH đk đúng, upload dữ liệu
//            Product product = new Product(name, desc, price, priceNewFormatted, sale, imgMainURL, imgSliderURL, popular, productType, imgOtherURL);

            // Tạo một sản phẩm mới với ID tự sinh
            Product product = new Product();
            product.setName(name);
            product.setDesc(desc);
            product.setPriceOld(price);
            product.setPriceNew(priceNewFormatted);
            product.setSale(sale);
            product.setImgURL(imgMainURL);
            product.setImgURlSlider(imgSliderURL);
            product.setPopular(popular);
            product.setProductType(productType);
            product.setImgURLOther(imgOtherURL);

            // Sinh id theo stt 1++ có node trên firebase để lưu trữ
            currentProductId++;
            currentIdReference.setValue(currentProductId);
            // Tạo ID trên giá trị node currentProductId
            String id = String.valueOf(currentProductId);

            FirebaseDatabase.getInstance().getReference("products").child(id)
                    .setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddDrinkAdminActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                Intent intent = new Intent(AddDrinkAdminActivity.this, DrinkPageAdminActivity.class);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(AddDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (IllegalArgumentException e) {
            dialog.dismiss();
            Toast.makeText(AddDrinkAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(AddDrinkAdminActivity.this, "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
