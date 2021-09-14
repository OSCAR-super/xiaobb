package com.example.springbootdemoentity.entity;


import lombok.Data;

@Data
public class User {
    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getOpenId() {
        return OpenId;
    }

    public void setOpenId(String openId) {
        OpenId = openId;
    }

    private int UserId;

    @Override
    public String toString() {
        return "User{" +
                "UserId=" + UserId +
                ", OpenId='" + OpenId + '\'' +
                '}';
    }

    private String OpenId;


}
