package com.my.chatting.chatV3.chattingRoom.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateRoom {
    private String title;
    private int number;
}
