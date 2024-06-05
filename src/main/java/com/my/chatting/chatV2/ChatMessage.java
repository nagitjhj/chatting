package com.my.chatting.chatV2;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMessage {
    private String sender;
    private String recipient;
    private String content;
}
