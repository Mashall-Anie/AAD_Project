package com.example.android_food_app.FragmentAdmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android_food_app.ActivityAdmin.DessertPageAdminActivity;
import com.example.android_food_app.ActivityAdmin.DrinkPageAdminActivity;
import com.example.android_food_app.ActivityAdmin.FoodPageAdminActivity;
import com.example.android_food_app.AdapterAdmin.HomeAdminAdapter;
import com.example.android_food_app.AdapterUser.PhotoAdapterViewPager2;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator3;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeAdminFragment extends Fragment {
    private ViewPager2 viewPager2;
    private SearchView searchView;
    private CircleIndicator3 indicator3;
    private PhotoAdapterViewPager2 adapter;
    private List<Product> list;
    private List<Product> listProductHome;
    private RecyclerView rcv_home_admin;
    private HomeAdminAdapter adapterHomeAdmin;
    private CircleImageView category_image_food, category_image_drink, category_image_dessert;
    private Handler handler = new Handler(Looper.getMainLooper());

    // Runnable là một giao diện đơn giản có một phương thức duy nhất là run().
    // Nó đại diện cho một tác vụ có thể thực thi.
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager2.getCurrentItem() == list.size() - 1) {
                viewPager2.setCurrentItem(0);
            } else {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
            handler.postDelayed(this, 3000); // Lặp lại runnable mỗi 3 giây
        }
    };

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrangMonNgonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeAdminFragment newInstance(String param1, String param2) {
        HomeAdminFragment fragment = new HomeAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo danh sách ảnh
        list = new ArrayList<>();
        getListPhoto();

        listProductHome = new ArrayList<>();
        getListProductHome();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        viewPager2 = view.findViewById(R.id.viewPager2);
        category_image_food = view.findViewById(R.id.category_image_food);
        category_image_drink = view.findViewById(R.id.category_image_drink);
        category_image_dessert = view.findViewById(R.id.category_image_dessert);
        indicator3 = view.findViewById(R.id.indicator);
        rcv_home_admin = view.findViewById(R.id.rcv_home_admin);
        searchView = view.findViewById(R.id.searchView);

        category_image_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FoodPageAdminActivity.class);
                startActivity(intent);
            }
        });

        category_image_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DrinkPageAdminActivity.class);
                startActivity(intent);
            }
        });

        category_image_dessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DessertPageAdminActivity.class);
                startActivity(intent);
            }
        });

        //khởi tạo adapter cho rcv_home_admin
        adapterHomeAdmin = new HomeAdminAdapter(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),2);
        rcv_home_admin.setLayoutManager(gridLayoutManager);
        rcv_home_admin.setFocusable(false);
        rcv_home_admin.setNestedScrollingEnabled(false);

        adapterHomeAdmin.setData(listProductHome);
        rcv_home_admin.setAdapter(adapterHomeAdmin);

        // Khởi tạo adapter và thiết lập cho ViewPager2
        adapter = new PhotoAdapterViewPager2(requireActivity(), list);
        viewPager2.setAdapter(adapter);
        indicator3.setViewPager(viewPager2);
        // Handler và runnable sẽ thực hiện khi ViewPager2 chuyển page
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Remove runnable cũ và post runnable mới
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        });

        // Bắt đầu handler lần đầu tiên
        handler.postDelayed(runnable, 3000);

        return view;
    }
    private void getListPhoto() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if(product != null && product.getPopular() == true) {
                    list.add(product);
                    adapter.notifyDataSetChanged();
                    indicator3.createIndicators(adapter.getItemCount(), 0);
                }

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getName().equals(product.getName())) {
                            list.set(i, product);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // khi xóa 1 item
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getName().equals(product.getName())) {
                            list.remove(i);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    indicator3.createIndicators(adapter.getItemCount(), 0);
                }

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getListProductHome() {
        List<Product> listProductHome = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");

        // Sử dụng query để lấy các sản phẩm có trường popular = true
        myRef.orderByChild("popular").equalTo(true).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    listProductHome.add(product);
                    adapterHomeAdmin.setData(listProductHome); // Cập nhật dữ liệu vào adapter
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi có sự thay đổi dữ liệu
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    for (int i = 0; i < listProductHome.size(); i++) {
                        if (listProductHome.get(i).getName().equals(product.getName())) {
                            listProductHome.set(i, product);
                            break;
                        }
                    }
                    adapterHomeAdmin.setData(listProductHome);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi có sự xóa dữ liệu
                Product product = snapshot.getValue(Product.class);
                if (product == null || listProductHome == null || listProductHome.isEmpty()) {
                    return;
                }
                for(int i = 0; i< listProductHome.size(); i++) {
                    if(product.getName() == listProductHome.get(i).getName()){
                        listProductHome.remove(listProductHome.get(i));
                        break;
                    }
                }
                adapterHomeAdmin.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi có sự di chuyển dữ liệu
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
            }
        });

    }
    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product item : listProductHome) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapterHomeAdmin.setData(filteredList);
        adapterHomeAdmin.notifyDataSetChanged();
    }


    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }
}