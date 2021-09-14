package com.example.springboot.demo.controller;

import com.alibaba.fastjson.JSONObject;

import com.example.springboot.demo.mq.HttpRequest;
import com.example.springboot.demo.mq.RabbitProducer;
import com.example.springboot.demo.service.UserService;
import com.example.springbootdemoentity.entity.*;

import com.example.wxpay.sdk.MyConfig;
import com.example.wxpay.sdk.WXPay;
import com.example.wxpay.sdk.WXPayUtil;
import com.zhenzi.sms.ZhenziSmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RabbitProducer rabbitProducer;

    @RequestMapping(value = "/doUnifiedOrder")
    public String doUnifiedOrder(String total_fee,String openid,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject result = new JSONObject();
        Map resultMap=new HashMap();

        MyConfig config = null;
        WXPay wxpay =null;
        try {
            config = new MyConfig();
            wxpay= new WXPay(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //生成的随机字符串
        String nonce_str = WXPayUtil.generateNonceStr();
        //获取客户端的ip地址
        //获取本机的ip地址
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String spbill_create_ip = addr.getHostAddress();
        //支付金额，需要转成字符串类型，否则后面的签名会失败

        //商品描述
        String body = "福大小帮帮";
        //商户订单号
        String out_trade_no= WXPayUtil.generateNonceStr();
        //统一下单接口参数
        Map<String, String> data = new HashMap<String, String>();
        data.put("appid","wxcc3cc053b5de036a");
        data.put("mch_id",String.valueOf(1603097268).replace(" ",""));
        data.put("nonce_str", nonce_str);
        System.out.println(nonce_str);
        System.out.println(body);
        System.out.println(out_trade_no);
        System.out.println(total_fee);
        System.out.println(spbill_create_ip);
        data.put("body", body);
        data.put("out_trade_no",out_trade_no);
        data.put("total_fee", total_fee);
        data.put("spbill_create_ip", "101.132.138.83");
        data.put("notify_url", "https://www.gaihelp.cn/demo/b");
        data.put("trade_type","JSAPI");
        data.put("openid", openid);
        System.out.println(data);
        try {
            Map<String, String> rMap = wxpay.unifiedOrder(data);
            System.out.println("统一下单接口返回: " + rMap);
            String return_code = (String) rMap.get("return_code");
            String result_code = (String) rMap.get("result_code");
            String nonceStr = WXPayUtil.generateNonceStr();
            resultMap.put("nonceStr", nonceStr);

            Long timeStamp = System.currentTimeMillis() / 1000;
            if ("SUCCESS".equals(return_code) && return_code.equals(result_code)) {
                String prepayid = rMap.get("prepay_id");
                resultMap.put("package", "prepay_id="+prepayid);
                resultMap.put("signType", "MD5");
                //这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                resultMap.put("timeStamp", timeStamp + "");
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                resultMap.put("appId","wxcc3cc053b5de036a");
                String sign = WXPayUtil.generateSignature(resultMap, "cx13579cx13579cx13579cx13579ABCD");
                resultMap.put("paySign", sign);
                System.out.println("生成的签名paySign : "+ sign);
                result.put("nonceStr", nonceStr);
                result.put("package", "prepay_id="+prepayid);
                result.put("signType", "MD5");
                result.put("timeStamp", timeStamp + "");
                result.put("appId","wxcc3cc053b5de036a");
                result.put("paySign", sign);
                System.out.println(resultMap);
                return result.toString();
            }else{
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result.toString();
        }
    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @RequestMapping(value = "/getOpenid")
    public String getOpenid(String code,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if (code == null || code.length() == 0) {
            return "code 不能为空";
        }
        //小程序的appid
        String appId = "wxcc3cc053b5de036a";
        // 小程序的secret
        String appsecret = "31d67bf91014758cc357a219aa915ba2";

        //向微信服务器 使用登录凭证 code 获取 session_key 和 openid
        // 请求参数
        String params = "appid=" + appId + "&secret=" + appsecret + "&js_code=" + code + "&grant_type=authorization_code";

        // 发送请求
        String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);

        JSONObject jsonObject = JSONObject.parseObject(sr);

        System.out.println(jsonObject.get("openid"));

        JSONObject result1 = new JSONObject();
        result1.put("openid",jsonObject.get("openid"));
        return result1.toString();
    }
    @RequestMapping(value = "/b")
    public String b(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
    return "bbb";
    }
        @RequestMapping(value = "/yOpenid")
    public String yOpenid(String OpenId,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        User user=userService.getUser(OpenId);
        if (user!=null){
            return "no";
        }else {

            return "yes";
        }
    }
    @RequestMapping(value = "/subOrder")
    public String subOrder(String Prices,String Room,String OpenId,String OrderPhone,String AllPrice,String Name,String OrderTime, String OrderProduct,String Address, String aq,String isUp,String Note ,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Date date = new Date();
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        System.out.println(dateString+"|");
        List<Order> orders=userService.getAllOrderDayly(dateString);
        System.out.println("uuu"+orders.size());
        String size=userService.getlimit();
        if (orders.size()>Integer.parseInt(size)){
            return "fail";
        }else {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        int ab=userService.getAllOrderDayly(dateString).size();

        String abb= String.valueOf(userService.getAllOrderDayly(dateString).size());
            System.out.println(ab);

        if (Integer.toString(ab).length()==1){
            abb="00"+abb;
        }else if(Integer.toString(ab).length()==2){
            abb="0"+abb;
        }
        String orderId=simpleDateFormat.format(new Date())+abb;
        User user=userService.getUser(OpenId);
        if (user!=null){
            System.out.println("usernotnull");
        }else {
            userService.addUser(OpenId);
        }
        rabbitProducer.sendTopicTopicB(orderId,OrderTime+"："+Name+"："+OrderPhone+"："+AllPrice+"："+Address+"："+OpenId+"："+isUp+"："+Note+"："+Room+"："+Prices,aq);
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
        JSONObject result = new JSONObject();
        result.put("product",OrderProduct);
        jedis.append(orderId,result.toString());
        JSONObject result1 = new JSONObject();
        result1.put("orderid",orderId);
        aq = aq.replace("\"", "");
        jedis.append(orderId+"xiaobb",aq);
        return result1.toString();
        }
    }
    @RequestMapping(value = "/achieveOrder")
    public String achieveOrder(String OrderId,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.achieveOrder(OrderId);
        return "ok";
    }
    @RequestMapping(value = "/Login")
    public String Login(String UserName,String Password,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Admin admin=userService.Login(UserName,Password);
        JSONObject result = new JSONObject();
        if (admin!=null){
            result.put("username",UserName);
            result.put("loginstate","success");
        }else {
            result.put("loginstate","fail");
        }
        return result.toString();
    }
    @RequestMapping(value = "/Exc")
    public String Exc(String UserName,String Password,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Admin admin=userService.Login(UserName,Password);
        JSONObject result = new JSONObject();
        if (admin!=null){

            result.put("Status","success");
        }else {
            result.put("Status","fail");
        }
        return result.toString();
    }
    @RequestMapping(value = "/updatePwd")
    public String updatePwd(String UserName,String Password,String PasswordNew,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.updatePwd(UserName,Password,PasswordNew);
        Admin admin=userService.Login(UserName,PasswordNew);
        JSONObject result = new JSONObject();
        if (admin!=null){
            result.put("updatestate","success");
        }else {
            result.put("updatestate","fail");
        }
        return result.toString();
    }
    @RequestMapping(value = "/delOrder")
    public String delOrder(String OrderId,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.delOrder(OrderId);
        return "ok";
    }
    @RequestMapping(value = "/updateLimit")
    public String updateLimit(String limit,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.updateLimit(limit);
        JSONObject result = new JSONObject();
        result.put("status","success");
        return result.toString();
    }
    @RequestMapping(value = "/getAnnounce")
    public String getAnnounce(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String i=userService.getannounce();
        JSONObject result = new JSONObject();
        result.put("announce",i);
        return result.toString();
    }
    @RequestMapping(value = "/getAvailable")
    public String getAvailable(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Available available=userService.getAvailable();
        JSONObject result = new JSONObject();
        result.put("available",available);
        return result.toString();
    }
    @RequestMapping(value = "/updateAvailable")
    public String updateAvailable(String todayStart,String todayEnd,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.updateAvailable(todayStart,todayEnd);
        JSONObject result = new JSONObject();
        result.put("state","success");
        return result.toString();
    }
    @RequestMapping(value = "/updateAnnounce")
    public String updateAnnounce(String announce,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.updateannounce(announce);
        JSONObject result = new JSONObject();
        result.put("status","success");
        return result.toString();
    }

    @RequestMapping(value = "/getdefautprice")
    public String getdefautprice(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<Price> prices = userService.getdefaut();
        JSONObject result = new JSONObject();
        result.put("price",prices);
        return result.toString();
    }
        @RequestMapping(value = "/getdefaut")
    public String getdefaut(HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<Price>prices=userService.getdefaut();
        List<String> strings=new ArrayList<>();
        strings.add("https://gaihelp.cn/demo/showEInvoice?FileName=1.jpg");
        strings.add("https://gaihelp.cn/demo/showEInvoice?FileName=2.jpg");
        strings.add("https://gaihelp.cn/demo/showEInvoice?FileName=3.jpg");
        JSONObject result = new JSONObject();
        result.put("serviceUrl","https://gaihelp.cn/demo/showEInvoice?FileName=4.jpg");
        result.put("indexImgUrl","https://gaihelp.cn/demo/showEInvoice?FileName=5.jpg");
        result.put("orderImgsUrl",strings);
        result.put("price",prices);
            String i=userService.getannounce();
            result.put("announce",i);
            Available available=userService.getAvailable();
            result.put("available",available);
            String size=userService.getlimit();
            result.put("limit",size);
        return result.toString();
    }
    @RequestMapping(value = "/getPicture")
    public String getPicture(HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");

        List<String> strings=new ArrayList<>();
        strings.add("https://gaihelp.cn/demo/showEInvoice?FileName=1.jpg");
        strings.add("https://gaihelp.cn/demo/showEInvoice?FileName=2.jpg");
        strings.add("https://gaihelp.cn/demo/showEInvoice?FileName=3.jpg");
        strings.add("https://gaihelp.cn/demo/showEInvoice?FileName=4.jpg");
        strings.add("https://gaihelp.cn/demo/showEInvoice?FileName=5.jpg");
        JSONObject result = new JSONObject();

        result.put("orderImgsUrl",strings);

        return result.toString();
    }
    @RequestMapping(value = "/cencelOrder")
    public String cencelOrder(String OrderId,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.cencelOrder(OrderId);
        return "ok";
    }
    @RequestMapping(value = "/getOrderProduct")
    public String getOrderProduct(String OrderId,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
        String i=jedis.get(OrderId);
        return i;
    }
    @RequestMapping(value = "/getAllUserOrder")
    public String getAllUserOrder(String OpenId,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<Order> orders=userService.getAllUserOrder(OpenId);
        JSONObject result= new JSONObject();
        result.put("allorder",orders);
        return result.toString();
    }
    @RequestMapping(value = "/yAllOrderDayly")
    public String yAllOrderDayly(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Date date = new Date();
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        List<Order> orders=userService.getAllOrderDayly(dateString);
        String size=userService.getlimit();
        if (orders.size()>Integer.parseInt(size)){
            return "no";
        }else {
            return "yes";
        }
    }
        @RequestMapping(value = "/getAllOrderDayly")
    public String getAllOrderDayly(String Day,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<Order> orders=userService.getAllOrderDayly(Day);
        List<Order> order=userService.getAllOrder();
        Double totalPrice=0.0;
        Double totalcost=0.0;
        JSONObject result= new JSONObject();
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        for (Order i:orders){
            String aq=jedis.get(i.getOrderId()+"xiaobb");
            String[] a=aq.split(";");
            System.out.println(a.length);
            for (int ii=0;ii<a.length;ii+=3){
                System.out.println(ii);
                totalPrice+=Double.parseDouble(a[ii+2]);
                totalcost+=Double.parseDouble(a[ii+1]);
            }
        }
        result.put("totalPrice",totalPrice);
        result.put("totalcost",totalcost);
        result.put("totalearn",formatDouble4(totalPrice-totalcost));
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
        for (Order o:orders){
            String i=jedis.get(o.getOrderId());
            o.setOrderState(i);
        }
        result.put("allorder",orders);
        result.put("totoal",order.size());
        result.put("DailyTotal",orders.size());
        return result.toString();
    }
    public static String formatDouble4(double d) {
        DecimalFormat df = new DecimalFormat("#.00");


        return df.format(d);
    }
    @RequestMapping(value = "/getAllOrder")
    public String getAllOrder(HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<Order> orders=userService.getAllOrder();
        JSONObject result= new JSONObject();
        result.put("allorder",orders);

        return result.toString();
    }
    @CrossOrigin
    @RequestMapping(value = "showEInvoice")
    public String showEInvoice(String FileName,HttpServletRequest request,HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        String face=FileName;
        FileInputStream fis = null;
        OutputStream os = null;
        System.out.println(face);
        String filepath = "C:\\upload\\"+face;     //path是你服务器上图片的绝对路径
        File file = new File(filepath);
        if(file.exists()){
            try {
                fis = new FileInputStream(file);
                long size = file.length();
                byte[] temp = new byte[(int) size];
                fis.read(temp, 0, (int) size);
                fis.close();
                byte[] data = temp;
                response.setContentType("image/jpg");
                os = response.getOutputStream();
                os.write(data);
                os.flush();
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "显示完成";
    }
    @RequestMapping(value = "/addAddress")
    public String addAddress(String OrderName,String OrderPhone,String OpenId,String Address,String Room,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.addAddress(OpenId,Address,Room,OrderPhone,OrderName);
        return "ok";
    }
    @RequestMapping(value = "/updateAddress")
    public String updateAddress(String OrderName,String OrderNameb,String OrderPhone,String OrderPhoneb,String OpenId,String Address,String Addressb,String Room,String Roomb,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.updateAddress(OpenId,Address,Addressb,Room,Roomb,OrderPhone,OrderPhoneb,OrderName,OrderNameb);
        System.out.println(OpenId+Address+Room+OrderPhone+OrderName);
        return "ok";
}
    @RequestMapping(value = "/upadtePrice")
    public String upadtePrice(String cosup, String cosdown,String weight,String up,String down,String Address,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.upadtePrice(weight,up,down,cosup,cosdown);

        return "success";
    }
    @RequestMapping(value = "/findAddress")
    public String findAddress(String OpenId,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<Address>addresses=userService.findAddress(OpenId);
        JSONObject result= new JSONObject();
        result.put("alladdress",addresses);
        return result.toString();
    }
    @RequestMapping(value = "/getOrderAll")
    public String getOrderAll(HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Double totalPrice=0.0;
        Double totalcost=0.0;
        JSONObject result= new JSONObject();
        List<Order>order=userService.getAllOrder();
        result.put("ordermassage",order);
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        for (Order i:order){


        String aq=jedis.get(i.getOrderId()+"xiaobb");
        String[] a=aq.split(";");

        for (int ii=0;ii<a.length;ii+=3){
            totalPrice+=Double.parseDouble(a[ii+3]);
            totalcost+=Double.parseDouble(a[ii+2]);
        }
        }
        result.put("totalPrice",totalPrice);
        result.put("totalcost",totalcost);
        result.put("totalearn",totalPrice-totalcost);
        return result.toString();
    }
    @RequestMapping(value = "/getOrder")
    public String getOrder(String OrderId,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Order order=userService.getOrderId(OrderId);
        JSONObject result= new JSONObject();
        result.put("ordermassage",order);
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        String aq=jedis.get(OrderId+"xiaobb");
        String[] a=aq.split(";");
        Double totalPrice=0.0;
        Double totalcost=0.0;
        for (int ii=0;ii<a.length;ii+=3){
            totalPrice+=Integer.parseInt(a[ii+3]);
            totalcost+=Integer.parseInt(a[ii+2]);
        }
        result.put("totalPrice",totalPrice);
        result.put("totalcost",totalcost);
        result.put("totalearn",totalPrice-totalcost);
        return result.toString();
    }
    @CrossOrigin
    @RequestMapping(value = "/sendMessage")
    public String sendMessage(String Id ,String[] number,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println(number);

        for (String numbers:number){
            System.out.println(numbers);
            numbers = numbers.replace("[\"", "");
            numbers = numbers.replace("\"]", "");
            numbers = numbers.replace("\"", "");
            rabbitProducer.sendFanout(Id+"-"+numbers);
    }
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
        Date date = new Date();
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        List<String>errornumber = new ArrayList<>();
        if (jedis.exists(dateString)){
        String n=jedis.get(dateString);
        
        errornumber= Arrays.asList(n.split("-"));}
        JSONObject result= new JSONObject();
        result.put("success",number.length-errornumber.size());
        result.put("error",errornumber.size());
        result.put("errornumber",errornumber);
        return result.toString();
    }
    @CrossOrigin
    @RequestMapping(value = "downFile")
    public void downFile( MultipartFile file,String fileName, HttpServletResponse response, HttpServletRequest request)throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        String path="C:\\upload";


        System.out.println(file.getSize());
        InputStream inputStream = null;
        File files = null;
        try {
            files = File.createTempFile("temp", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.transferTo(files);   //sourceFile为传入的MultipartFile
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream = new FileInputStream(files);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        files.deleteOnExit();

        OutputStream os = null;






        try {

            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len=1024;
            // 输出的文件流保存到本地文件
            File tempFile = new File(path);

            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
            // 开始读取

            int i=0;

            while (true) {
                len = inputStream.read(bs) ;
                System.out.println(len);
                if (len==-1){
                    break;
                }
                i++;
                System.out.println(i);
                os.write(bs, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        request.setAttribute("news", fileName+"上传成功,谢谢分享");

        String UserFace=fileName;
        System.out.println(fileName);


    }

}

