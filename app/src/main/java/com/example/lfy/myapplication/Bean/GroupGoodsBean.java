package com.example.lfy.myapplication.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/9 0009.
 */
public class GroupGoodsBean implements Serializable{
    String tuanid;
    String title;//火龙果
    String img;///images/titiles/红提.jpg
    String detail;//
    double tuanPrice;
    String marketPrice;
    String singlePrice;
    double cost;
    String Standard;//500g
    String Place;//越南
    String personNum;//3
    String tuanCount;
    String ShelfState;


    public String getTuanid() {
        return tuanid;
    }

    public void setTuanid(String tuanid) {
        this.tuanid = tuanid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getTuanPrice() {
        return tuanPrice;
    }

    public void setTuanPrice(double tuanPrice) {
        this.tuanPrice = tuanPrice;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(String singlePrice) {
        this.singlePrice = singlePrice;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getStandard() {
        return Standard;
    }

    public void setStandard(String standard) {
        Standard = standard;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public String getPersonNum() {
        return personNum;
    }

    public void setPersonNum(String personNum) {
        this.personNum = personNum;
    }

    public String getTuanCount() {
        return tuanCount;
    }

    public void setTuanCount(String tuanCount) {
        this.tuanCount = tuanCount;
    }

    public String getShelfState() {
        return ShelfState;
    }

    public void setShelfState(String shelfState) {
        ShelfState = shelfState;
    }
}
