package com.example.lfy.myapplication.Bean;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class Evaluate {
    private String evaluateID;
    private String orderID;
    private String customerID;
    private String pointID;
    private String evaluateStr;
    private String creatTime;
    private float distributionStar;
    private float serviceStar;
    private String evaluateText = "";
    private String CustomerName;
    private float star;

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getEvaluateID() {
        return evaluateID;
    }

    public void setEvaluateID(String evaluateID) {
        this.evaluateID = evaluateID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getEvaluateStr() {
        return evaluateStr;
    }

    public void setEvaluateStr(String evaluateStr) {
        this.evaluateStr = evaluateStr;
    }

    public String getPointID() {
        return pointID;
    }

    public void setPointID(String pointID) {
        this.pointID = pointID;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public float getDistributionStar() {
        return distributionStar;
    }

    public void setDistributionStar(float distributionStar) {
        this.distributionStar = distributionStar;
    }

    public float getServiceStar() {
        return serviceStar;
    }

    public void setServiceStar(float serviceStar) {
        this.serviceStar = serviceStar;
    }

    public String getEvaluateText() {
        return evaluateText;
    }

    public void setEvaluateText(String evaluateText) {
        this.evaluateText = evaluateText;
    }
}
