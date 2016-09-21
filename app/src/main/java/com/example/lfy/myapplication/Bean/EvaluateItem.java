package com.example.lfy.myapplication.Bean;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class EvaluateItem {
    private String productName;
    private String evaluateText = "";
    private String productId;
    private float Star;
    private String Image;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getEvaluateText() {
        return evaluateText;
    }

    public void setEvaluateText(String evaluateText) {
        this.evaluateText = evaluateText;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public float getStar() {
        return Star;
    }

    public void setStar(float star) {
        Star = star;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

}
