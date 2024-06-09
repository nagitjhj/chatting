package com.my.chatting.chatV3.chattingRoom.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessage {
    private String sender;
    private String senderId;
    private String content;
    private String date;
}
