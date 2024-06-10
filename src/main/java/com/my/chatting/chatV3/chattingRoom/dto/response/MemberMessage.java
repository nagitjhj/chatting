package com.my.chatting.chatV3.chattingRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class MemberMessage {
    private String nickname;
    private boolean isEnter;

    public MemberMessage(boolean isEnter) {
        this.isEnter = isEnter;
    }
}
