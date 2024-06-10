package com.my.chatting.chatV3.chattingRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseChattingRoomListInfo {
    private String roomId;
    private String title;
    private int number;
    private int memberCount;
}
