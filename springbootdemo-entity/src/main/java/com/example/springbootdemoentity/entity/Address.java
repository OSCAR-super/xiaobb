package com.example.springbootdemoentity.entity;

public class Address {
    private String OpenIdA;
    private String AddressA;
    private String RoomA;
    private String OrderPhoneA;

    public String getOpenIdA() {
        return OpenIdA;
    }

    public void setOpenIdA(String openIdA) {
        OpenIdA = openIdA;
    }

    public String getAddressA() {
        return AddressA;
    }

    public void setAddressA(String addressA) {
        AddressA = addressA;
    }

    public String getRoomA() {
        return RoomA;
    }

    public void setRoomA(String roomA) {
        RoomA = roomA;
    }

    public String getOrderPhoneA() {
        return OrderPhoneA;
    }

    public void setOrderPhoneA(String orderPhoneA) {
        OrderPhoneA = orderPhoneA;
    }

    public String getOrderName() {
        return OrderName;
    }

    public void setOrderName(String orderName) {
        OrderName = orderName;
    }

    @Override
    public String toString() {
        return "Address{" +
                "OpenIdA='" + OpenIdA + '\'' +
                ", AddressA='" + AddressA + '\'' +
                ", RoomA='" + RoomA + '\'' +
                ", OrderPhoneA='" + OrderPhoneA + '\'' +
                ", OrderName='" + OrderName + '\'' +
                '}';
    }

    private String OrderName;



}
