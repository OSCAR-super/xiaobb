package com.example.springboot.demo.dao;



import com.example.springbootdemoentity.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserMapper {

    Order getOrderId(@Param("orderId")String orderId);
    User getUser(@Param("OpenId") String openId);

    void setOrder(@Param("OrderId")String orderId, @Param("OrderTime")String orderTime,@Param("Name") String name,@Param("OrderPhone") String orderPhone,@Param("AllPrice") String allPrice,@Param("Address")String address,@Param("OpenId")String openId,@Param("isUp")String isUp,@Param("Note")String Note,@Param("Room")String Room,@Param("Prices")String Prices);

    List<Order> getAllUserOrder(@Param("OpenId")String openId);

    void addUser(@Param("OpenId")String openId);

    void addAddress(@Param("OpenId")String openId, @Param("Address")String address,@Param("Room")String Room,@Param("OrderPhone")String OrderPhone,@Param("OrderName")String OrderName);

    List<Address> findAddress(@Param("OpenId")String openId);

    void updateAddress(@Param("OpenId")String openId, @Param("Address")String address,@Param("Addressb")String addressb,@Param("Room")String Room,@Param("Roomb")String Roomb,@Param("OrderPhone")String OrderPhone,@Param("OrderPhoneb")String OrderPhoneb,@Param("OrderName")String OrderName,@Param("OrderNameb")String OrderNameb);

    void delAddress(@Param("OpenId")String openId, @Param("Address")String address);

    void avhieve(@Param("OrderId")String orderId);

    void delOrder(@Param("OrderId")String orderId);

    void cencelOrder(@Param("OrderId")String orderId);

    Admin Login(@Param("UserName")String userName, @Param("Password")String password);

    void updatePwd(@Param("UserName")String userName,@Param("Password") String password,@Param("PasswordNew") String passwordNew);

    List<Order> getAllOrderDayly(@Param("day")String day);

    List<Order> getAllOrder();

    List<Price> getdefaut();

    void upadtePrice(@Param("weight")String weight,@Param("up") String up, @Param("down")String down,@Param("cosup")String cosup,@Param("cosdown") String cosdown);

    String getlimit();

    void updateLimit(@Param("size")String limit);

    void updateannounce(@Param("announce")String announce);

    String getannounce();

    void updateAvailable(@Param("todayStart")String todayStart, @Param("todayEnd")String todayEnd);

    Available getAvailable();
}
