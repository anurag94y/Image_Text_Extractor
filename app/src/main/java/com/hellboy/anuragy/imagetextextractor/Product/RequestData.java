package com.hellboy.anuragy.imagetextextractor.Product;

import java.util.List;

/**
 * Created by anurag.y on 23/06/17.
 */

public class RequestData {
    List<ItemData> itemList;
    String pincode;
    double totalAmount;

    public List<ItemData> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemData> itemList) {
        this.itemList = itemList;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
