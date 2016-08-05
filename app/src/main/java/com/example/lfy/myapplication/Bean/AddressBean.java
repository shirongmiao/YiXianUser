package com.example.lfy.myapplication.Bean;

import java.io.Serializable;

/**
 * Created by lfy on 2015/12/22.
 */
public class AddressBean implements Serializable {
    public String id;
    public String customerID;
    public String name;
    public String pointname;
    public String city;
    public String phone;
    public String district;//小区
    public String address;//详细地址
    public String sex;//提示
    public String Isdefault;//提示

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPointname() {
        return pointname;
    }

    public void setPointname(String pointname) {
        this.pointname = pointname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIsdefault() {
        return Isdefault;
    }

    public void setIsdefault(String isdefault) {
        Isdefault = isdefault;
    }
}
