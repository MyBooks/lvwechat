package com.lv.study.lvwechat.controller;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.hibernate.exception.DataException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.security.MessageDigest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/WX")
@Slf4j
public class WXController {

    private String token  = "1997@LVlv";

    @GetMapping("/checkWX")
    public String checkWXDevelop(String signature,String timestamp,String nonce,String echostr) throws Exception {
        if (checkAuthor(timestamp, nonce,signature)){
            return echostr;
        }else{
            return null;
        }
    }

    @PostMapping("/checkWX")
    public String checkWXDevelopPost(String signature, String timestamp, String nonce, HttpServletRequest request,String ToUserName, HttpServletResponse httpServletResponse) throws Exception {
        if (checkAuthor(timestamp, nonce,signature)){
            SAXReader reader = new SAXReader();
            Document read = reader.read(request.getInputStream());
            Element rootElement = read.getRootElement();
            Element element = rootElement.element("MsgType");
            if ("text".equals(element.getText())) {
                Document document = DocumentHelper.createDocument();
                Element xml = document.addElement("xml");
                Element toUserName = xml.addElement("ToUserName");
                toUserName.setText("oKjk-5z6KAJPk1VoGZCKJkvwOmhc");
                Element fromUserName = xml.addElement("FromUserName");
                fromUserName.setText(rootElement.elementText("ToUserName"));
                Element createTime = xml.addElement("CreateTime");
                createTime.setData(new Date());
                Element msgType = xml.addElement("MsgType");
                msgType.setText("text");
                Element content = xml.addElement("Content");
                content.setText("复制："+rootElement.elementText("Content"));
                String xmlEncoding = document.asXML();
                log.info("xml:"+xmlEncoding);
                return xmlEncoding;
            }
            return null;
        }else{
            return null;
        }
    }

    private boolean checkAuthor(String timestamp, String nonce,String signature) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        log.info("timestamp:{}",new Date(Long.parseLong(timestamp)));
        List<String> strings = Arrays.asList("1997@LVlv", nonce, timestamp);
        strings.stream().sorted().forEach(e->{
            stringBuffer.append(e);
        });
        String str = stringBuffer.toString();
        String s = shaEncode(str);
        return signature.equals(s);
    }


    public static String shaEncode(String inStr) throws Exception {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}
