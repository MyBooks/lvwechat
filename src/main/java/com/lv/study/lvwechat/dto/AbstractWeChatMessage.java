package com.lv.study.lvwechat.dto;

import lombok.Data;

@Data
public abstract class AbstractWeChatMessage {

    // 消息来源
    private String toUserName;

    // 消息类别
    private String fromUserName;

}
