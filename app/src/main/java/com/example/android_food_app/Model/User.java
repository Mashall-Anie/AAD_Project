package com.example.android_food_app.Model;

public class User {
    private int resourceId;
    private String name, desc;

    public User(int resourceId, String name, String desc) {
        this.resourceId = resourceId;
        this.name = name;
        this.desc = desc;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
