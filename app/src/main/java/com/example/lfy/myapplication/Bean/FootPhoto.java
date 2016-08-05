package com.example.lfy.myapplication.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lfy on 2016/6/3.
 */
public class FootPhoto implements Serializable {

    String ActivityImg;
    String Type;
    String Typename;
    List<GridPhoto> Products;

    public String getActivityImg() {
        return ActivityImg;
    }

    public void setActivityImg(String activityImg) {
        ActivityImg = activityImg;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTypename() {
        return Typename;
    }

    public void setTypename(String typename) {
        Typename = typename;
    }

    public List<GridPhoto> getProducts() {
        return Products;
    }

    public void setProducts(List<GridPhoto> products) {
        Products = products;
    }
}
