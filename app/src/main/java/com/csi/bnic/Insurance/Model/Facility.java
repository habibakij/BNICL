package com.csi.bnic.Insurance.Model;

/**
 * Created by Jahid on 22/9/19.
 */
public class Facility {
    String id;
    String name;
    String cost;
    public Facility(String id, String name, String cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
