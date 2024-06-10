package com.my.chatting.chatV3.chattingRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseRoomMemberClose {
    private String roomId;
    private String sessionId;
    private int countMember;
    private boolean isHost;
    private String nextHostSessionId;

    public ResponseRoomMemberClose(String roomId, String sessionId) {
        this.roomId = roomId;
        this.sessionId = sessionId;
    }
}
