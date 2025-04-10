package com.example.android_food_app.AdapterUser;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.ActivityUser.CreateOrderActivity;
import com.example.android_food_app.ActivityUser.DetailPageUserActivity;
import com.example.android_food_app.ActivityUser.ShipmentDetailPageActivity;
import com.example.android_food_app.Model.Customer;
import com.example.android_food_app.R;

import java.util.List;

public class ShipAddressRecycleviewAdapter extends RecyclerView.Adapter<ShipAddressRecycleviewAdapter.ShipAddressViewHolder>{
    private List<Customer> listCustomer;
    private Context mConText;
    private  IClickListener iClickListener;
    public interface IClickListener {
        void onClickUpdateItem(Customer customer);
        void onClickDeleteItem(Customer customer);
        void onClick(Customer customer);
    }

    public ShipAddressRecycleviewAdapter(List<Customer> listCustomer, IClickListener iClickListener) {
        this.listCustomer = listCustomer;
        this.iClickListener = iClickListener;
    }

    @NonNull
    @Override
    public ShipAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_user, parent, false);
        return  new ShipAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShipAddressViewHolder holder, int position) {
        Customer customer = listCustomer.get(position);
        if(customer == null) {
            return;
        }
        holder.edit_text_email.setText("Email: " + customer.getEmailuser());
        holder.edit_text_id.setText("ID: " + customer.getId());
        holder.edit_text_name.setText("Họ và tên: " + customer.getName());
        holder.edit_text_phone.setText("Số điện thoại: " + customer.getPhone());
        holder.edit_text_address.setText("Địa chỉ: " + customer.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.onClick(customer);

            }
        });
        holder.btn_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.onClickUpdateItem(customer);
            }
        });

        holder.btn_delete_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.onClickDeleteItem(customer);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listCustomer != null) {
            return  listCustomer.size();
        }
        return 0;
    }

    public class  ShipAddressViewHolder extends RecyclerView.ViewHolder {
        private EditText edit_text_id, edit_text_name, edit_text_phone, edit_text_address, edit_text_email;
        private Button btn_edit_address,btn_delete_address;

        public ShipAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            edit_text_email = itemView.findViewById(R.id.edit_text_email);
            edit_text_id = itemView.findViewById(R.id.edit_text_id);
            edit_text_name = itemView.findViewById(R.id.edit_text_name);
            edit_text_phone = itemView.findViewById(R.id.edit_text_phone);
            edit_text_address = itemView.findViewById(R.id.edit_text_address);

            btn_edit_address = itemView.findViewById(R.id.btn_edit_address);
            btn_delete_address = itemView.findViewById(R.id.btn_delete_address);



        }
    }

}
