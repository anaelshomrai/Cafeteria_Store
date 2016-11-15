package com.cafeteria.cafeteria_store.data;

/**
 * Created by ashom on 15-Nov-16.
 */

public interface InventoryItem {

    int getId();
    String getTitle();
    boolean isInStock();

    void setId(int id);
    void setTitle(String title);
    void setInStock(boolean inStock);

}
