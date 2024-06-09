package com.my.chatting.chatV3.chattingRoom.controller;

import com.my.chatting.chatV3.chattingRoom.dto.*;
import com.my.chatting.chatV3.chattingRoom.repository.ChattingRoomRepository;
import jakarta.annotation.Nullable;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate template;
    private final ChattingRoomRepository repository;

    @MessageMapping("/room/{roomId}")
    public void roomEnterMember(@DestinationVariable("roomId") String roomId, SimpMessageHeaderAccessor accessor) {
        ChattingRoom room = repository.findById(roomId);
        ResponseRoomInfo roomInfo = new ResponseRoomInfo(
                roomId,
                room.getNumber(),
                room.getMemberList().size(),
                room.getHost(),
                accessor.getSessionId(),
                room.getMemberList()
        );

        template.convertAndSend("/room/member/"+roomId, roomInfo);
    }

    @MessageMapping("{roomId}")
    public void roomChat(@DestinationVariable("roomId") String roomId, SendMessage message, SimpMessageHeaderAccessor accessor) {
        message.setSenderId(accessor.getSessionId());
        template.convertAndSend("/topic/"+roomId, message);
    }

    @MessageMapping("/room/room-list")
    public void roomChat(SimpMessageHeaderAccessor accessor) {
        Map<String, ChattingRoom> room = repository.findAll();
        template.convertAndSendToUser(accessor.getSessionId(), "/queue/room", room, createHeaders(accessor.getSessionId()));
//        template.convertAndSend("/room/room-list", room);
    }

    @MessageMapping("/close")
    public void roomClose(Map<String, String> message, SimpMessageHeaderAccessor accessor) {
//        log.info("왔어요");
        ChattingRoom chattingRoom = repository.findById(message.get("roomId"));

        if(chattingRoom == null) {
            return;
        }

        //남은 멤버 1명이 나가면 방 지우기
        if(chattingRoom.getMemberList().size() == 1) {
            repository.delete(message.get("roomId"));
            template.convertAndSend("/room/room-list/delete-room", message.get("roomId"));
            return;
        }

        if(chattingRoom.getHost().equals(accessor.getSessionId())){
            chattingRoom.getMemberList().remove(0);
            chattingRoom.setHost(chattingRoom.getMemberList().get(0).getId());
            chattingRoom.getMemberList().get(0).setHost(true);
        }else{
            for(Member m : chattingRoom.getMemberList()){
                if(m.getId().equals(accessor.getSessionId())){
                    chattingRoom.getMemberList().remove(m);
                    break;
                }
            }
        }

        // room-list 멤버 업뎃
        template.convertAndSend("/room/room-list/update-room", new ResponseRoomMember(
                message.get("roomId"), chattingRoom.getNumber(), chattingRoom.getMemberList().size()
        ));

        // room 멤버 업뎃
        ResponseRoomInfo roomInfo = new ResponseRoomInfo(
                message.get("roomId"),
                chattingRoom.getNumber(),
                chattingRoom.getMemberList().size(),
                chattingRoom.getHost(),
                accessor.getSessionId(),
                chattingRoom.getMemberList()
        );

        template.convertAndSend("/room/member/"+message.get("roomId"), roomInfo);
    }

    private MessageHeaders createHeaders(@Nullable String sessionId){
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        if(sessionId != null) {
            headerAccessor.setSessionId(sessionId);
        }
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

}
