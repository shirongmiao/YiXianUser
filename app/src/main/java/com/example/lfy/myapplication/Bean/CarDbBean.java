package com.example.lfy.myapplication.Bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by lfy on 2016/6/10.
 */
//Image string,
// ProductID string,
// Title string,
// Price string,
// count INTEGER,
// PromotionPrice string,
// Standard string,
// Cost string
@Table(name = "YiXian_car")
public class CarDbBean implements Serializable {

    @Column(name = "Cost")
    private double Cost;

    @Column(name = "CustomerID")
    private String CustomerID;

    @Column(name = "Image")
    private String Image;

    @Column(name = "Price")
    private double Price;

    @Column(name = "ProductCount")
    private int ProductCount;

    @Column(name = "ProductID", isId = true)
    private String ProductID;

    @Column(name = "Standard")
    private String Standard;

    @Column(name = "PromotionName")
    private String PromotionName;

    @Column(name = "PromotionPrice")
    private double PromotionPrice;

    @Column(name = "Title")
    private String Title;

    @Column(name = "Type1")
    private String Type1;

    @Column(name = "Type3")
    private String Type3;

    @Column(name = "TypeName1")
    private String TypeName1;

    @Column(name = "point")
    private String point;

    public String getType3() {
        return Type3;
    }

    public void setType3(String type3) {
        Type3 = type3;
    }

    public double getCost() {
        return Cost;
    }

    public void setCost(double cost) {
        Cost = cost;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getProductCount() {
        return ProductCount;
    }

    public void setProductCount(int productCount) {
        ProductCount = productCount;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getStandard() {
        return Standard;
    }

    public void setStandard(String standard) {
        Standard = standard;
    }

    public String getPromotionName() {
        return PromotionName;
    }

    public void setPromotionName(String promotionName) {
        PromotionName = promotionName;
    }

    public double getPromotionPrice() {
        return PromotionPrice;
    }

    public void setPromotionPrice(double promotionPrice) {
        PromotionPrice = promotionPrice;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
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

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}