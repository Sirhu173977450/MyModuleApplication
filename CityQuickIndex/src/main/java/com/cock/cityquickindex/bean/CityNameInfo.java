package com.cock.cityquickindex.bean;

/**
 * Author : Created by Luhailiang on 2016/10/30 15:20.
 * Email : 18336094752@163.com
 */

public class CityNameInfo {
    private int id;
    private int parent;
    private String name;
    private int type;

    public CityNameInfo() {
        super();
    }

    public CityNameInfo(int id, int parent, String name) {
        this.id = id;
        this.parent = parent;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
