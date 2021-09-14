package com.example.springbootdemoentity.entity;

public class Order {


    private String OrderId;
    private String OrderState;




    private String OrderPhone;
    private String OrderTime;
    private String Address;
    private String AllPrice;
    private String Name;



    private String isUp;

    @Override
    public String toString() {
        return "Order{" +
                "OrderId='" + OrderId + '\'' +
                ", OrderState='" + OrderState + '\'' +
                ", OrderPhone='" + OrderPhone + '\'' +
                ", OrderTime='" + OrderTime + '\'' +
                ", Address='" + Address + '\'' +
                ", AllPrice='" + AllPrice + '\'' +
                ", Name='" + Name + '\'' +
                ", isUp='" + isUp + '\'' +
                ", Note='" + Note + '\'' +
                ", Room='" + Room + '\'' +
                ", Prices='" + Prices + '\'' +
                '}';
    }

    private String Note;






    private String Room;

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderState() {
        return OrderState;
    }

    public void setOrderState(String orderState) {
        OrderState = orderState;
    }

    public String getOrderPhone() {
        return OrderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        OrderPhone = orderPhone;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAllPrice() {
        return AllPrice;
    }

    public void setAllPrice(String allPrice) {
        AllPrice = allPrice;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIsUp() {
        return isUp;
    }

    public void setIsUp(String isUp) {
        this.isUp = isUp;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getPrices() {
        return Prices;
    }

    public void setPrices(String prices) {
        Prices = prices;
    }

    private String Prices;

}
