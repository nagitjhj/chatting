package com.my.chatting.chatV3.chattingRoom.controller;

import com.my.chatting.chatV3.chattingRoom.dto.*;
import com.my.chatting.chatV3.chattingRoom.dto.request.RequestSendMessage;
import com.my.chatting.chatV3.chattingRoom.dto.response.MemberMessage;
import com.my.chatting.chatV3.chattingRoom.dto.response.ResponseRoomInfo;
import com.my.chatting.chatV3.chattingRoom.dto.response.ResponseRoomMember;
import com.my.chatting.chatV3.chattingRoom.dto.response.ResponseRoomMemberClose;
import com.my.chatting.chatV3.chattingRoom.repository.ChattingRoomRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

        template.convertAndSendToUser(accessor.getSessionId(), "/queue/room/member/"+roomId, roomInfo, createHeaders(accessor.getSessionId()));
    }

    @MessageMapping("{roomId}")
    public void roomChat(@DestinationVariable("roomId") String roomId, RequestSendMessage message, SimpMessageHeaderAccessor accessor) {
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

        ResponseRoomMemberClose responseRoomMemberClose = new ResponseRoomMemberClose(message.get("roomId"), accessor.getSessionId());
        MemberMessage memberMessage = new MemberMessage(false);

        if(chattingRoom.getHost().equals(accessor.getSessionId())){
            memberMessage.setNickname(chattingRoom.getMemberList().get(0).getNickname());

            chattingRoom.getMemberList().remove(0);
            chattingRoom.setHost(chattingRoom.getMemberList().get(0).getId());
            chattingRoom.getMemberList().get(0).setHost(true);

            responseRoomMemberClose.setHost(true);
            responseRoomMemberClose.setNextHostSessionId(chattingRoom.getMemberList().get(0).getId());
        }else{
            responseRoomMemberClose.setHost(false);
            for(Member m : chattingRoom.getMemberList()){
                if(m.getId().equals(accessor.getSessionId())){
                    memberMessage.setNickname(m.getNickname());
                    chattingRoom.getMemberList().remove(m);
                    break;
                }
            }
        }

        // room-list 멤버 업뎃
        template.convertAndSend("/room/room-list/update-room", new ResponseRoomMember(
                message.get("roomId"), chattingRoom.getNumber(), chattingRoom.getMemberList().size()
        ));
        responseRoomMemberClose.setCountMember(chattingRoom.getMemberList().size());

        // room 멤버 나간것....
        template.convertAndSend("/room/member/close/"+message.get("roomId"), responseRoomMemberClose);
        template.convertAndSend("/topic/member/"+message.get("roomId"), memberMessage);
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
