package com.csi.bnic.Insurance.Model;

import org.json.JSONArray;

/**
 * Created by Jahid on 4/8/19.
 */
public class Vehicle {
    String name;
    String id;
    JSONArray jsonArraySeat;
    public Vehicle(String name, String id, JSONArray jsonArraySeat) {
        this.name = name;
        this.id = id;
        this.jsonArraySeat = jsonArraySeat;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONArray getJsonArraySeat() {
        return jsonArraySeat;
    }

    public void setJsonArraySeat(JSONArray jsonArraySeat) {
        this.jsonArraySeat = jsonArraySeat;
    }
}
