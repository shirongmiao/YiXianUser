package com.example.lfy.myapplication.Bean;

import java.io.Serializable;

/**
 * Created by lfy on 2016/3/30.
 */
public class Baidu_Bean implements Serializable {
    public String ID;
    public String Name;
    public String District;//下沙区
    public String Longitude;//经度
    public String Latitude;//纬度
    public String Address;//成蹊苑41幢楼下佰分鲜
    public String Time;//营业时间
    public String Phone;//电话

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
