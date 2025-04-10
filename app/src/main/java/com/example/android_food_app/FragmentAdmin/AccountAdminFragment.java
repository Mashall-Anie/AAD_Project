package com.example.android_food_app.FragmentAdmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_food_app.Activity.ChangePasswordActivity;
import com.example.android_food_app.Activity.LoginActivity;
import com.example.android_food_app.ActivityAdmin.RevenuePageAdminActivity;
import com.example.android_food_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountAdminFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ProgressDialog progressDialog;

    public AccountAdminFragment() {
        // Required empty public constructor
    }

    public static AccountAdminFragment newInstance(String param1, String param2) {
        AccountAdminFragment fragment = new AccountAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_admin, container, false);
        LinearLayout revenueLayout = view.findViewById(R.id.revenue_layout);
        LinearLayout changePassword = view.findViewById(R.id.changePassword);
        LinearLayout logoutLayout = view.findViewById(R.id.logout);
        TextView email = view.findViewById(R.id.txt_email);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Đang đăng xuất...");
        // Lấy thông tin người dùng hiện tại
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            email.setText(userEmail);
        }

        revenueLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RevenuePageAdminActivity.class);
                startActivity(intent);
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                FirebaseAuth.getInstance().signOut();
                progressDialog.dismiss(); // Tắt progress dialog khi đã đăng xuất

                // Hiển thị thông báo đăng xuất thành công
                Toast.makeText(getActivity(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                // Kết thúc hoạt động hiện tại để ngăn người dùng quay lại bằng nút back
                getActivity().finish();
            }
        });

        return view;
    }
}
