package com.example.android_food_app.Fragment.FragmentBottomNavigationUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import com.example.android_food_app.ActivityUser.DrinkPageUserActivity;
import com.example.android_food_app.ActivityUser.FoodPageUserActivity;
import com.example.android_food_app.ActivityUser.DessertPageUserActivity;
import com.example.android_food_app.AdapterUser.HomeUserRecycleViewAdapter;
import com.example.android_food_app.Model.Product;
import com.example.android_food_app.Model.Photo;
import com.example.android_food_app.AdapterUser.PhotoAdapterViewPager2;
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
 * Use the {@link HomeUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeUserFragment extends Fragment {
    private ViewPager2 viewPager2;
    private CircleIndicator3 indicator3;
    private PhotoAdapterViewPager2 adapter;
    private List<Product> list;
    private List<Product> listProductHome;
    private RecyclerView rcv_trangchu;
    private HomeUserRecycleViewAdapter adapter_trangchu;
    private CircleImageView category_image_food;
    private CircleImageView category_image_drink;
    private CircleImageView category_image_dessert;
    private SearchView searchView;

    // Handler để cập nhật giao diện người dùng từ các luồng nền
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeUserFragment() {
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
    public static HomeUserFragment newInstance(String param1, String param2) {
        HomeUserFragment fragment = new HomeUserFragment();
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

        listProductHome = new ArrayList<>();
        getListProductHome();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);

        // Ánh xạ ViewPager2 và CircleIndicator3
        viewPager2 = view.findViewById(R.id.viewPager2);
        indicator3 = view.findViewById(R.id.indicator);
        rcv_trangchu = view.findViewById(R.id.rcv_trangchu);
        searchView = view.findViewById(R.id.searchView);


        // Khởi tạo adapter và thiết lập cho ViewPager2
        getListPhoto();
        adapter = new PhotoAdapterViewPager2(requireActivity(), list);
        viewPager2.setAdapter(adapter);
        indicator3.setViewPager(viewPager2);

        //Ánh xạ CircleImageView
        category_image_food = view.findViewById(R.id.category_image_food);
        category_image_drink = view.findViewById(R.id.category_image_drink);
        category_image_dessert = view.findViewById(R.id.category_image_dessert);


        //bắt sự kiện khi ấn vào CircleImageView
        category_image_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_monngon = new Intent(getActivity(), FoodPageUserActivity.class);
                startActivity(intent_monngon);

            }
        });

        category_image_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_douong = new Intent(getActivity(), DrinkPageUserActivity.class);
                startActivity(intent_douong);

            }
        });

        category_image_dessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_trangmieng = new Intent(getActivity(), DessertPageUserActivity.class);
                startActivity(intent_trangmieng);

            }
        });

        //khởi tạo adapter cho rcv_trangchu
        adapter_trangchu = new HomeUserRecycleViewAdapter(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),2);
        rcv_trangchu.setLayoutManager(gridLayoutManager);
        rcv_trangchu.setFocusable(false);
        rcv_trangchu.setNestedScrollingEnabled(false);

        adapter_trangchu.setDataProduct(listProductHome);
        rcv_trangchu.setAdapter(adapter_trangchu);


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

        // Thiết lập SearchView để tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });


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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");

        // Sử dụng query để lấy các sản phẩm có trường popular = true
        myRef.orderByChild("popular").equalTo(true).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    listProductHome.add(product);
                    adapter_trangchu.setDataProduct(listProductHome); // Cập nhật dữ liệu vào adapter
                    adapter_trangchu.notifyDataSetChanged();
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
                    adapter_trangchu.setDataProduct(listProductHome);
                    adapter_trangchu.notifyDataSetChanged();
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
                adapter_trangchu.notifyDataSetChanged();
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
        adapter_trangchu.setDataProduct(filteredList);
        adapter_trangchu.notifyDataSetChanged();
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
