package com.example.android_food_app.AdapterAdmin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.android_food_app.FragmentAdmin.OrderAdminFragment;
import com.example.android_food_app.FragmentAdmin.HomeAdminFragment;
import com.example.android_food_app.FragmentAdmin.FeedbackAdminFragment;
import com.example.android_food_app.FragmentAdmin.AccountAdminFragment;


public class ViewPager2Adapter extends FragmentStateAdapter {
    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeAdminFragment();
            case 1:
                return new FeedbackAdminFragment();
            case 2:
                return new OrderAdminFragment();
            case 3:
                return new AccountAdminFragment();
            default:
                return new HomeAdminFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
