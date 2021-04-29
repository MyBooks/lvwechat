package com.lv.study.lvwechat.controller;

import io.swagger.annotations.*;
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
@Api(tags = "微信消息分发入口",value = "WXController")
public class WXController {

    private String token  = "1997@LVlv";

    @ApiOperation(value = "接受消息绑定接口",notes = "校验消息过程，将token和timestamp以及nonce混合按字母顺序排序，sha加密后与签名验证返回echoStr")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signature",value = "参数签名",required = true,dataType = "String",paramType = "query",example = "ea4481dd43830274d141c89b50e47248f93d81a9"),
            @ApiImplicitParam(name = "timestamp",value = "消息发送时间",required = true,dataType = "String",paramType = "query", example="1619708401"),
            @ApiImplicitParam(name = "nonce",value = "随机字符串",required = true,dataType = "String" ,paramType = "query",example = "akkFddqqwEq"),
            @ApiImplicitParam(name = "echoStr",value = "回复字段",required = true,dataType = "String",paramType = "query",example = "hello")}
    )
    @GetMapping("/acceptMessage")
    public String checkWXDevelop(String signature,String timestamp,String nonce,String echoStr) {
        if (checkAuthor(timestamp, nonce,signature)){
            return echoStr;
        }else{
            return null;
        }
    }

    @ApiOperation(value = "接收消息接口",notes = "校验消息过程，将token和timestamp以及nonce混合按字母顺序排序，sha加密后与签名验证返回echoStr")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signature",value = "参数签名",required = true,dataType = "String",example = "ea4481dd43830274d141c89b50e47248f93d81a9"),
            @ApiImplicitParam(name = "timestamp",value = "消息发送时间",required = true,dataType = "String",example = "1619708401"),
            @ApiImplicitParam(name = "nonce",value = "随机字符串",required = true,dataType = "String",example = "akkFddqqwEq")}
    )
    @PostMapping("/acceptMessage")
    public String checkWXDevelopPost(String signature, String timestamp, String nonce, @ApiParam(hidden = true) HttpServletRequest request) throws Exception {
        if (checkAuthor(timestamp, nonce,signature)){
            SAXReader reader = new SAXReader();
            Document read = reader.read(request.getInputStream());
            Element rootElement = read.getRootElement();
            // 消息转发处理

            Element element = rootElement.element("MsgType");
            if("text".equals(element.getText())) {
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

    /**
     * 检验消息正确性
     *
     * @param timestamp 发送时间
     * @param nonce 噪音
     * @param signature 验证的签名
     * @return boolean 消息是否为真
     */
    private boolean checkAuthor(String timestamp, String nonce,String signature) {
        try{
            StringBuffer stringBuffer = new StringBuffer();
            log.info("消息发送时间 timestamp:{}",new Date(Long.parseLong(timestamp)));
            List<String> strings = Arrays.asList("1997@LVlv", nonce, timestamp);
            strings.stream().sorted().forEach(e->{
                stringBuffer.append(e);
            });
            String s = shaEncode(stringBuffer.toString());
            log.info("签名:{},加密值：{}",signature,s);
            return signature.equals(s);
        } catch (Exception e) {
            log.error("消息加密失败",e);
        }
        return false;
    }


    /**
     * SHA加密
     *
     * @param inStr 加密的字符串
     * @return String 加密后的字符串
     * @throws Exception 加密异常
     */
    public static String shaEncode(String inStr) throws Exception {
        MessageDigest sha = null;
        sha = MessageDigest.getInstance("SHA");
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
