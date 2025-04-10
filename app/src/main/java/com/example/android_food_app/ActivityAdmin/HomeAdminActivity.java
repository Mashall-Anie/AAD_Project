package com.example.android_food_app.ActivityAdmin;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_food_app.FragmentAdmin.ViewPager2AdminAdapter;
import com.example.android_food_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeAdminActivity extends AppCompatActivity {
    private ViewPager2 mViewPager2;
    private BottomNavigationView mbottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Anh xa
        mViewPager2 = findViewById(R.id.view_pager_2);
        mbottomNavigationView = findViewById(R.id.bottom_navigation);

        ViewPager2AdminAdapter adapter = new ViewPager2AdminAdapter(this);
        mViewPager2.setAdapter(adapter);
        mViewPager2.setOffscreenPageLimit(3);

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                        break;
                    case 1:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_desc).setChecked(true);
                        break;
                    case 2:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_order).setChecked(true);
                        break;
                    case 3:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_account).setChecked(true);
                        break;
                }
            }
        });

        mbottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.menu_home) {
                    mViewPager2.setCurrentItem(0);
                } else if (itemId == R.id.menu_desc) {
                    mViewPager2.setCurrentItem(1);
                } else if (itemId == R.id.menu_order) {
                    mViewPager2.setCurrentItem(2);
                } else if (itemId == R.id.menu_account) {
                    mViewPager2.setCurrentItem(3);
                }

                return true;
            }
        });


    }
}