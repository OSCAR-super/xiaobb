package com.example.wxpay.sdk;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @Description: 微信支付配置类
 * @Author: hm
 * @CreateDate: 2018/12/10 17:19.
 * @UpdateDate: 2018/12/10 17:19.
 * @Version: 1.0
 */
public class MyConfig extends WXPayConfig {

    private byte[] certData;
    public MyConfig() throws Exception {
//        String certPath = "classpath:apiclient_cert.p12";
//        File file = new File(certPath);
//        InputStream certStream = new FileInputStream(file);
//        this.certData = new byte[(int) file.length()];
//        certStream.read(this.certData);
//        certStream.close();
    }

    @Override
    public String getAppID() {
        return "wxcc3cc053b5de036a";
    }

    @Override
    public String getMchID() {
        return "1603097268";
    }

    @Override
    public String getKey() {
        return "cx13579cx13579cx13579cx13579ABCD";
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {

            public void report(String domain, long elapsedTimeMillis, Exception ex) {
            }


            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com", false);
            }
        };
    }
}
