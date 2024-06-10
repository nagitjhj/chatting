package com.my.chatting.chatV3.chattingRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseRoomMemberPlus {
    private String roomId;
    private String sessionId;
    private String nickname;
    private int countMember;

    public ResponseRoomMemberPlus(String roomId, String sessionId) {
        this.roomId = roomId;
        this.sessionId = sessionId;
    }
}
