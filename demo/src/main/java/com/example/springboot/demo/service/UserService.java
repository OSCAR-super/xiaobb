package com.example.springboot.demo.service;


import com.example.springboot.demo.dao.UserMapper;
import com.example.springbootdemoentity.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class UserService implements Serializable {
    @Autowired
    private UserMapper userMapper;

    public Order getOrderId(String orderId) {
        return userMapper.getOrderId(orderId);
    }
    public User getUser(String openId) {
        return userMapper.getUser(openId);
    }

    public void setOrder(String OrderId, String OrderTime, String Name, String OrderPhone, String AllPrice,String Address,String OpenId,String isUp,String Note,String OrderNumber,String Prices) {
        userMapper.setOrder(OrderId,OrderTime,Name,OrderPhone,AllPrice,Address,OpenId,isUp,Note,OrderNumber,Prices);
    }

    public List<Order> getAllUserOrder(String openId) {
        return userMapper.getAllUserOrder(openId);
    }

    public void addUser(String openId) {
        userMapper.addUser(openId);
    }

    public void addAddress(String openId, String address,String Room,String OrderPhone,String OrderName) {
        userMapper.addAddress(openId,address,Room,OrderPhone,OrderName);
    }

    public List<Address> findAddress(String openId) {
        return userMapper.findAddress(openId);
    }

    public void updateAddress(String openId, String address,String addressb,String Room,String Roomb,String OrderPhone,String OrderPhoneb,String OrderName,String OrderNameb) {
        userMapper.updateAddress(openId,address,addressb,Room,Roomb,OrderPhone,OrderPhoneb,OrderName,OrderNameb);
    }

    public void delAddress(String openId, String address) {
        userMapper.delAddress(openId,address);
    }

    public void achieveOrder(String orderId) {
        userMapper.avhieve(orderId);
    }

    public void delOrder(String orderId) {
        userMapper.delOrder(orderId);
    }

    public void cencelOrder(String orderId) {
        userMapper.cencelOrder(orderId);
    }

    public Admin Login(String userName, String password) {
        return userMapper.Login(userName,password);
    }

    public void updatePwd(String userName, String password, String passwordNew) {
    userMapper.updatePwd(userName,password,passwordNew);
    }

    public List<Order> getAllOrderDayly(String day) {
        return userMapper.getAllOrderDayly(day);
    }

    public List<Order> getAllOrder() {
        return userMapper.getAllOrder();
    }

    public List<Price> getdefaut() {
        return userMapper.getdefaut();
    }

    public void upadtePrice(String weight, String up, String down,String cosup, String cosdown) {
    userMapper.upadtePrice(weight,up,down,cosup,cosdown);
    }

    public String getlimit() {
        return userMapper.getlimit();
    }

    public void updateLimit(String limit) {
        userMapper.updateLimit(limit);
    }

    public void updateannounce(String announce) {
        userMapper.updateannounce(announce);
    }

    public String getannounce() {
        return userMapper.getannounce();
    }

    public void updateAvailable(String todayStart, String todayEnd) {
    userMapper.updateAvailable(todayStart,todayEnd);
    }

    public Available getAvailable() {
        return userMapper.getAvailable();
    }
}
