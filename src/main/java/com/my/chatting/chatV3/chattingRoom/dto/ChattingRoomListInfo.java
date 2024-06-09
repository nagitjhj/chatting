package com.my.chatting.chatV3.chattingRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChattingRoomListInfo {
    private String roomId;
    private String title;
    private int number;
    private int memberCount;
}
