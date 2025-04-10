package com.example.android_food_app.ActivityUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_food_app.AdapterUser.BottomAdapterViewpager2;
import com.example.android_food_app.Fragment.FragmentBottomNavigationUser.CartUserFragment;
import com.example.android_food_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeUserActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private BottomNavigationView btn_navigation;
    private BottomAdapterViewpager2 adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //ánh xạ ID
        viewPager2 = findViewById(R.id.view_pager2);
        btn_navigation = findViewById(R.id.btn_navigation);

        adapter = new BottomAdapterViewpager2(this);
        viewPager2.setAdapter(adapter);
        viewPager2.setOffscreenPageLimit(3);

        //chuyển page của viewpager thì bottom phải chuyển tương ứng và ngược lại

        //vuối viewpager2
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        btn_navigation.getMenu().findItem(R.id.menu_home).setChecked(true);
                        break;
                    case 1:
                        btn_navigation.getMenu().findItem(R.id.menu_cart).setChecked(true);
                        break;
                    case 2:
                        btn_navigation.getMenu().findItem(R.id.menu_comment).setChecked(true);
                        break;
                    case 3:
                        btn_navigation.getMenu().findItem(R.id.menu_contact).setChecked(true);
                        break;
                    case 4:
                        btn_navigation.getMenu().findItem(R.id.menu_user).setChecked(true);
                        break;
                }
            }
        });
        btn_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_home) {
                    viewPager2.setCurrentItem(0);
                } else if (menuItem.getItemId() == R.id.menu_cart) {
                    viewPager2.setCurrentItem(1);
                } else if (menuItem.getItemId() == R.id.menu_comment) {
                    viewPager2.setCurrentItem(2);
                } else if (menuItem.getItemId() == R.id.menu_contact) {
                    viewPager2.setCurrentItem(3);
                } else if (menuItem.getItemId() == R.id.menu_user) {
                    viewPager2.setCurrentItem(4);
                }

                return true;
            }
        });
        // Nhận Intent để mở fragment giỏ hàng nếu cần
        Intent intent = getIntent();
        if (intent != null) {
            boolean openCart = intent.getBooleanExtra("openCart", false);
            if (openCart) {
                viewPager2.setCurrentItem(1); // Chuyển đến fragment giỏ hàng
            }
        }

    }

}
