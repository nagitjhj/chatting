package com.my.chatting.chatV3.chattingRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChattingRoom {
    private String roomId;
    private String host;
    private String title;
    private int number;
    private List<Member> memberList;

    public ChattingRoom(String roomId, String title, int number) {
        this.roomId = roomId;
        this.title = title;
        this.number = number;
    }

    public void setMember(Member member){
        if(memberList == null){
            memberList = new ArrayList<>();
        }
        memberList.add(member);
    }
}
