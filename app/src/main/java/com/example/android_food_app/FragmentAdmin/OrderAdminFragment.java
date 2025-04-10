package com.example.android_food_app.FragmentAdmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.AdapterAdmin.OrderAdminAdapter;
import com.example.android_food_app.Model.Order;
import com.example.android_food_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderAdminFragment extends Fragment {
    private RecyclerView rcv_order;
    private OrderAdminAdapter mAdapter;
    private List<Order> mListOrder;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public OrderAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrangChuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderAdminFragment newInstance(String param1, String param2) {
        OrderAdminFragment fragment = new OrderAdminFragment();
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
        View view = inflater.inflate(R.layout.fragment_order_admin, container, false);
        rcv_order = view.findViewById(R.id.rcv_order);
        mAdapter = new OrderAdminAdapter(getContext());
        rcv_order.setAdapter(mAdapter);
        rcv_order.setLayoutManager(new LinearLayoutManager(getContext()));
        initData();
        mAdapter.setData(mListOrder);
        return view;


    }

    private void initData() {
        mListOrder = new ArrayList<>();

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListOrder.clear();

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    // Lấy thông tin của đơn hàng và thêm vào danh sách mListOrder
                    Order order = orderSnapshot.getValue(Order.class);
                    mListOrder.add(order);
                }

                mAdapter.setData(mListOrder);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy xuất dữ liệu từ Firebase
            }
        });
    }



}