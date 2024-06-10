package com.my.chatting.chatV3.chattingRoom.dto.response;

import com.my.chatting.chatV3.chattingRoom.dto.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResponseRoomInfo {
    private String roomId;
    private int number;
    private int memberCount;
    private String host;
    private String mySession;
    private List<Member> memberList;
}
