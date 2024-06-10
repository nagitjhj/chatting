package com.my.chatting.chatV3.chattingRoom.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSendMessage {
    private String sender;
    private String senderId;
    private String content;
    private String date;
}
