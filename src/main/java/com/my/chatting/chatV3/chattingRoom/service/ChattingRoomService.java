package com.my.chatting.chatV3.chattingRoom.service;

import com.my.chatting.chatV3.chattingRoom.dto.ChattingRoom;
import com.my.chatting.chatV3.chattingRoom.dto.request.RequestCreateRoom;
import com.my.chatting.chatV3.chattingRoom.repository.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChattingRoomService {
    private final ChattingRoomRepository repository;

    public ChattingRoom createChattingRoom(RequestCreateRoom room) {
        String roomId = String.valueOf(System.currentTimeMillis());

        ChattingRoom chattingRoom = new ChattingRoom(roomId, room.getTitle(), room.getNumber(), room.getPw());
        repository.save(roomId, chattingRoom);
        return chattingRoom;
    }

}
