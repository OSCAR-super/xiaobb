package com.example.springbootdemoentity.entity;

public class Price {
    private String weight;
    private String up;
    private String down;
    private String cosup;
    private String cosdown;

    @Override
    public String toString() {
        return "Price{" +
                "weight='" + weight + '\'' +
                ", up='" + up + '\'' +
                ", down='" + down + '\'' +
                ", cosup='" + cosup + '\'' +
                ", cosdown='" + cosdown + '\'' +
                '}';
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
    }

    public String getCosup() {
        return cosup;
    }

    public void setCosup(String cosup) {
        this.cosup = cosup;
    }

    public String getCosdown() {
        return cosdown;
    }

    public void setCosdown(String cosdown) {
        this.cosdown = cosdown;
    }
}
