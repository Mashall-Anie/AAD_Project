package com.example.android_food_app.AdapterUser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.android_food_app.Fragment.FragmentBottomNavigationUser.CartUserFragment;
import com.example.android_food_app.Fragment.FragmentBottomNavigationUser.ContactUserFragment;
import com.example.android_food_app.Fragment.FragmentBottomNavigationUser.FeedbackUserFragment;
import com.example.android_food_app.Fragment.FragmentBottomNavigationUser.AccountUserFragment;
import com.example.android_food_app.Fragment.FragmentBottomNavigationUser.HomeUserFragment;

public class BottomAdapterViewpager2 extends FragmentStateAdapter {
    public BottomAdapterViewpager2(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeUserFragment();
            case 1: return new CartUserFragment();
            case 2: return new FeedbackUserFragment();
            case 3: return new ContactUserFragment();
            case 4: return new AccountUserFragment();
            default: return new HomeUserFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
