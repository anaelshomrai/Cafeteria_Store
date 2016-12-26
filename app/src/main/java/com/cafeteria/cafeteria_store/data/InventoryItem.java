package com.cafeteria.cafeteria_store.data;

/**
 * Created by ashom on 15-Nov-16.
 */

public interface InventoryItem {

    double getPrice();
    int getId();
    String getTitle();
    boolean isInStock();

    void setPrice(double price);
    void setId(int id);
    void setTitle(String title);
    void setInStock(boolean inStock);

}
