package com.hellboy.anuragy.imagetextextractor.Product;

/**
 * Created by anurag.y on 23/06/17.
 */

public class ItemData {
    String itemName;
    double itemPrice;
    double Quantity;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }
}
