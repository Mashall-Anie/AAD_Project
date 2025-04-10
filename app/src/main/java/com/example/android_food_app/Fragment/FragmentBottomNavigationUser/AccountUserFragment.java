package com.example.android_food_app.Fragment.FragmentBottomNavigationUser;

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
import com.example.android_food_app.ActivityUser.OrderHistoryActivity;
import com.example.android_food_app.ActivityUser.ShipmentDetailPageActivity;
import com.example.android_food_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountUserFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ProgressDialog progressDialog;

    public AccountUserFragment() {
        // Required empty public constructor
    }

    public static AccountUserFragment newInstance(String param1, String param2) {
        AccountUserFragment fragment = new AccountUserFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_user, container, false);
        LinearLayout logoutLayout = view.findViewById(R.id.logout);
        LinearLayout changePassword = view.findViewById(R.id.changePassword);
        LinearLayout orderHistory = view.findViewById(R.id.orderHistory_layout);
        LinearLayout shipmentDetail = view.findViewById(R.id.shipmentDetail);
        TextView email = view.findViewById(R.id.txt_email);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Đang đăng xuất...");

        // Lấy thông tin người dùng hiện tại
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            email.setText(userEmail);
        }

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


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
                startActivity(intent);
            }
        });
        shipmentDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShipmentDetailPageActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
