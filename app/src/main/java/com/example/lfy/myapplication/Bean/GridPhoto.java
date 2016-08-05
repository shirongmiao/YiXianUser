package com.example.lfy.myapplication.Bean;

import java.io.Serializable;

/**
 * Created by lfy on 2016/6/3.
 */
public class GridPhoto implements Serializable{

    String ActivityImg;
    String Image;
    int OrderCount;
    double Price;
    String ProductID;
    String PromotionName;
    double PromotionPrice;
    String ShelfState;
    String Standard;
    String Type1;
    String TypeName1;
    String TypeName2;
    String point;
    String Title;
    int inventory;
    String Place;
    int ShowType;
    double Cost;

    public float getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public int getShowType() {
        return ShowType;
    }

    public void setShowType(int showType) {
        ShowType = showType;
    }

    public double getCost() {
        return Cost;
    }

    public void setCost(double cost) {
        Cost = cost;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public String getActivityImg() {
        return ActivityImg;
    }

    public void setActivityImg(String activityImg) {
        ActivityImg = activityImg;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getOrderCount() {
        return OrderCount;
    }

    public void setOrderCount(int orderCount) {
        OrderCount = orderCount;
    }


    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getPromotionName() {
        return PromotionName;
    }

    public void setPromotionName(String promotionName) {
        PromotionName = promotionName;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public double getPromotionPrice() {
        return PromotionPrice;
    }

    public void setPromotionPrice(double promotionPrice) {
        PromotionPrice = promotionPrice;
    }

    public String getShelfState() {
        return ShelfState;
    }

    public void setShelfState(String shelfState) {
        ShelfState = shelfState;
    }

    public String getStandard() {
        return Standard;
    }

    public void setStandard(String standard) {
        Standard = standard;
    }

    public String getType1() {
        return Type1;
    }

    public void setType1(String type1) {
        Type1 = type1;
    }

    public String getTypeName1() {
        return TypeName1;
    }

    public void setTypeName1(String typeName1) {
        TypeName1 = typeName1;
    }

    public String getTypeName2() {
        return TypeName2;
    }

    public void setTypeName2(String typeName2) {
        TypeName2 = typeName2;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
