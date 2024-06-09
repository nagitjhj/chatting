package com.my.chatting.chatV3.chattingRoom.repository;

import com.my.chatting.chatV3.chattingRoom.dto.ChattingRoom;
import com.my.chatting.chatV3.chattingRoom.dto.ChattingRoomListInfo;
import com.my.chatting.chatV3.chattingRoom.dto.Member;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ChattingRoomRepository {
    private static final Map<String, ChattingRoom> store = new HashMap<>();

    public void save(String key, ChattingRoom room) {
        store.put(key, room);
    }

    public void delete(String key) {
        store.remove(key);
    }

    public ChattingRoom findById(String key) {
        return store.get(key);
    }

    public ChattingRoom updateMember(String key, Member member) {
        ChattingRoom chattingRoom = store.get(key);
        if(member.isHost()){
            chattingRoom.setHost(member.getId());
        }
        chattingRoom.setMember(member);
        return chattingRoom;
    }

    public Map<String, ChattingRoom> findAll() {
        return store;
    }

    public List<ChattingRoomListInfo> findChattingRoomListInfo() {
        List<ChattingRoomListInfo> infos = new ArrayList<>();
        store.forEach((key, value) -> {
            infos.add(new ChattingRoomListInfo(key, value.getTitle(), value.getNumber(), value.getMemberList().size()));
        });
        return infos;
    }

    public void deleteMember(String key, ChattingRoom room) {
        store.replace(key, room);
    }
}
