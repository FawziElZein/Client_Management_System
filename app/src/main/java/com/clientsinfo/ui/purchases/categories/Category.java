package com.clientsinfo.ui.purchases.categories;

import java.io.Serializable;

public class Category implements Serializable {

    private String name;
    private double price;
    private int color;

    public Category(String name, double price, int color) {
        this.name = name;
        this.price = price;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
