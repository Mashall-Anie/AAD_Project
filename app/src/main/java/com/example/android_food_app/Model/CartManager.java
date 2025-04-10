package com.example.android_food_app.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<Product, Integer> cartProducts = new HashMap<>();

    private CartManager() {
        cartProducts = new HashMap<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Product product) {
        if (cartProducts.containsKey(product)) {
            int currentQuantity = cartProducts.get(product);
            cartProducts.put(product, currentQuantity + 1);
        } else {
            cartProducts.put(product, 1);
        }
    }

    public void decreaseProductQuantity(Product product) {
        if (cartProducts.containsKey(product)) {
            int currentQuantity = cartProducts.get(product);
            if (currentQuantity > 1) {
                cartProducts.put(product, currentQuantity - 1);
            } else {
                cartProducts.remove(product);
            }
        }
    }

    public int getProductQuantity(Product product) {
        return cartProducts.getOrDefault(product, 0);
    }

    public double getLinePrice(Product product) {
        int quantity = getProductQuantity(product);
        return Double.parseDouble(product.getPriceNew()) * quantity;
    }


    public List<Product> getCartProducts() {
        return new ArrayList<>(cartProducts.keySet());
    }

    public void removeProduct(Product product) {
        if (cartProducts.containsKey(product)) {
            cartProducts.remove(product);
        }
    }
}
