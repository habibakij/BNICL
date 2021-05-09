package com.csi.bnic.Insurance.Model;

/**
 * Created by Jahid on 7/8/19.
 */
public class Quote {
    String title;
    String totalCost;
    public Quote(String title, String totalCost) {
        this.title = title;
        this.totalCost = totalCost;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }
}
