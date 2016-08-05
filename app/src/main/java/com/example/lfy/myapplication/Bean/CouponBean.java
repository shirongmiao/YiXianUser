package com.example.lfy.myapplication.Bean;

import java.io.Serializable;

/**
 * Created by lfy on 2015/12/28.
 */
public class CouponBean implements Serializable {
    String CouponID;//优惠券ID
    String CouponStart;
    String CouponEnd;
    String CouponUsed;//
    String CouponState;//状态
    String Customerid;//
    String name;//
    double CouponPrice;
    String CouponMan;
    String CouponInfo;

    public String getCouponID() {
        return CouponID;
    }

    public void setCouponID(String couponID) {
        CouponID = couponID;
    }

    public String getCouponStart() {
        return CouponStart;
    }

    public void setCouponStart(String couponStart) {
        CouponStart = couponStart;
    }

    public String getCouponEnd() {
        return CouponEnd;
    }

    public void setCouponEnd(String couponEnd) {
        CouponEnd = couponEnd;
    }

    public String getCouponUsed() {
        return CouponUsed;
    }

    public void setCouponUsed(String couponUsed) {
        CouponUsed = couponUsed;
    }

    public String getCouponState() {
        return CouponState;
    }

    public void setCouponState(String couponState) {
        CouponState = couponState;
    }

    public String getCustomerid() {
        return Customerid;
    }

    public void setCustomerid(String customerid) {
        Customerid = customerid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCouponPrice() {
        return CouponPrice;
    }

    public void setCouponPrice(double couponPrice) {
        CouponPrice = couponPrice;
    }

    public String getCouponMan() {
        return CouponMan;
    }

    public void setCouponMan(String couponMan) {
        CouponMan = couponMan;
    }

    public String getCouponInfo() {
        return CouponInfo;
    }

    public void setCouponInfo(String couponInfo) {
        CouponInfo = couponInfo;
    }
}
