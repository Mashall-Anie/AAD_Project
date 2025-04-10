package com.example.android_food_app.Fragment.FragmentBottomNavigationUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.ActivityUser.CreateOrderActivity;
import com.example.android_food_app.AdapterUser.CartAdapterRecycleView;
import com.example.android_food_app.Model.CartManager;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;

import java.util.ArrayList;
import java.util.List;

public class CartUserFragment extends Fragment implements CartAdapterRecycleView.OnQuantityChangeListener {
    private RecyclerView rcvCart;
    private CartAdapterRecycleView cartAdapter;
    private List<Product> cartProducts;
    private TextView txt_sum;
    private Button btn_add_order;

    public CartUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartProducts = new ArrayList<>(); // Initialize an empty list
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_user, container, false);

        rcvCart = view.findViewById(R.id.rcv_cart);
        txt_sum = view.findViewById(R.id.txt_sum);
        btn_add_order = view.findViewById(R.id.btn_add_order);

        rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapterRecycleView(cartProducts, this);
        rcvCart.setAdapter(cartAdapter);

        btn_add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateOrderActivity.class);
                double totalAmount = calculateTotal();
                intent.putExtra("TOTAL_AMOUNT", totalAmount);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartProducts();
    }

    private void loadCartProducts() {
        List<Product> updatedProducts = CartManager.getInstance().getCartProducts();
        cartProducts.clear();
        cartProducts.addAll(updatedProducts);
        cartAdapter.notifyDataSetChanged();
        calculateTotal();
    }

    private double calculateTotal() {
        double total = 0.0;
        for (Product product : cartProducts) {
            double price = CartManager.getInstance().getLinePrice(product);
            total += price;
        }

        txt_sum.setText(String.format("%.3f VND", total));
        return total;
    }

    private void clickOpenBottomSheetDialog() {
        // Implement logic to open bottom sheet dialog
    }

    @Override
    public void onQuantityChanged() {
        calculateTotal();
    }
}
