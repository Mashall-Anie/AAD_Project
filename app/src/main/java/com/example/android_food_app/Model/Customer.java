package com.example.android_food_app.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Customer implements Serializable {
    private String emailuser;
    private Long id;
    private String name;
    private String phone;
    private String address;

    public Customer() {
    }

    public Customer(String emailuser, Long id, String name, String phone, String address) {
        this.emailuser = emailuser;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getEmailuser() {
        return emailuser;
    }

    public void setEmailuser(String emailuser) {
        this.emailuser = emailuser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "emailuser='" + emailuser + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("phone", phone);
        result.put("address", address);
        return result;
    }

}
