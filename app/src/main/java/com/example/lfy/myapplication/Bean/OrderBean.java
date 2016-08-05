package com.example.lfy.myapplication.Bean;

import java.io.Serializable;

/**
 * Created by lfy on 2016/1/8.
 */
public class OrderBean implements Serializable {
    String OrderID;
    String OrderNO;
    String OrderPrice;
    String Discount;
    String PayedPrice;
    String OrderType;
    String Distribution;//自提点
    String ProductID;
    String Payed;
    String CreateTime;
    String PayTime;
    String CustomerID;
    String IfMakeUp;
    String CustomerSay;
    String SwiftNumber;
    String ProductStr;
    String CouponID;
    String isNextDay;
    String point;
    String Delivery;

    public String getDelivery() {
        return Delivery;
    }

    public void setDelivery(String delivery) {
        Delivery = delivery;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getOrderNO() {
        return OrderNO;
    }

    public void setOrderNO(String orderNO) {
        OrderNO = orderNO;
    }

    public String getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        OrderPrice = orderPrice;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getPayedPrice() {
        return PayedPrice;
    }

    public void setPayedPrice(String payedPrice) {
        PayedPrice = payedPrice;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    public String getDistribution() {
        return Distribution;
    }

    public void setDistribution(String distribution) {
        Distribution = distribution;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getPayed() {
        return Payed;
    }

    public void setPayed(String payed) {
        Payed = payed;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getPayTime() {
        return PayTime;
    }

    public void setPayTime(String payTime) {
        PayTime = payTime;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getIfMakeUp() {
        return IfMakeUp;
    }

    public void setIfMakeUp(String ifMakeUp) {
        IfMakeUp = ifMakeUp;
    }

    public String getCustomerSay() {
        return CustomerSay;
    }

    public void setCustomerSay(String customerSay) {
        CustomerSay = customerSay;
    }

    public String getSwiftNumber() {
        return SwiftNumber;
    }

    public void setSwiftNumber(String swiftNumber) {
        SwiftNumber = swiftNumber;
    }

    public String getProductStr() {
        return ProductStr;
    }

    public void setProductStr(String productStr) {
        ProductStr = productStr;
    }

    public String getCouponID() {
        return CouponID;
    }

    public void setCouponID(String couponID) {
        CouponID = couponID;
    }

    public String getIsNextDay() {
        return isNextDay;
    }

    public void setIsNextDay(String isNextDay) {
        this.isNextDay = isNextDay;
    }
}
