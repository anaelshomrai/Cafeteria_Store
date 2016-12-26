package com.cafeteria.cafeteria_store.data;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by anael on 14/11/16.
 */

public class Main implements Serializable,InventoryItem {
    /**
     * The id of this main item (auto generated by db)
     */
    private int id;

    /**
     * The title of this main item.
     * 1-3 words that describes the main item
     */
    private String title;

    private boolean inStock;


    /**
     * Returns the id of this main item
     * @return the id of this main item
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id for this main item
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the title of this main item
     * @return the title of this main item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title to this main item
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isInStock() {
        return inStock;
    }

    @Override
    public void setPrice(double price) {

    }

    @Override
    public double getPrice() {
        return 0;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}