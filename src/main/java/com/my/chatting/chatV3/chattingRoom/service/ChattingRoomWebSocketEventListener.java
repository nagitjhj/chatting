package com.my.chatting.chatV3.chattingRoom.service;

import com.my.chatting.chatV3.chattingRoom.dto.ChattingRoom;
import com.my.chatting.chatV3.chattingRoom.dto.Member;
import com.my.chatting.chatV3.chattingRoom.dto.Nickname;
import com.my.chatting.chatV3.chattingRoom.dto.ResponseRoomMember;
import com.my.chatting.chatV3.chattingRoom.repository.ChattingRoomRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChattingRoomWebSocketEventListener {
    private final ChattingRoomRepository repository;
    private final SimpMessagingTemplate template;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();

        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");
        List roomId1 = (List) nativeHeaders.get("roomId");

        if(roomId1 == null) return;

        String roomId = (String) roomId1.get(0);

        boolean isFirst = false;
        ChattingRoom room = repository.findById(roomId);
        Member member = new Member();
        member.setId(sessionId);

        if(room.getHost() == null) {
            isFirst = true;
        }

        if(room.getMemberList() == null){
            room.setHost(sessionId);
            member.setHost(true);
        }

        for(Nickname name : Nickname.getRandomNickName()){
            boolean flag = true;
            if(room.getMemberList() == null){
                member.setNickname(String.valueOf(name));
                break;
            }else{
                for(Member m : room.getMemberList()){
                    if(m.getNickname().equals(String.valueOf(name))) flag = false;
                }
                if(flag){
                    member.setNickname(String.valueOf(name));
                    break;
                }
            }
        }

        ChattingRoom chattingRoom = repository.updateMember(roomId, member);

        // Create headers map
//        Map<String, Object> headersMap = new HashMap<>();
//        headersMap.put("roomId", roomId);
//        headersMap.put("sessionId", sessionId);
//
//        // Convert headers map to MessageHeaders
//        MessageHeaders messageHeaders = new MessageHeaders(headersMap);
//
//        // Create message with headers
//        Message<ChattingRoom> message = MessageBuilder.createMessage(chattingRoom, messageHeaders);



//        template.convertAndSend("/room/"+roomId, chattingRoom, createHeaders(sessionId));
//        template.convertAndSend("/room/"+roomId, chattingRoom);
//        template.convertAndSend("/topic/stat", "1211");

        //방 처음 생성 시 호출함
        if(isFirst) {
//            Map<String, ChattingRoom> roomList = repository.findAll();
            template.convertAndSend("/room/room-list/create-room", chattingRoom);
        }else{
            template.convertAndSend("/room/room-list/update-room", new ResponseRoomMember(
                    roomId, chattingRoom.getNumber(), chattingRoom.getMemberList().size()
            ));
        }

        log.info("세션 연결 sessionId : {}", sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        log.info("세션 종료 sessionId : {}", sessionId);
    }

//    private MessageHeaders createHeaders(@Nullable String sessionId){
//        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
//        if(sessionId != null) {
//            headerAccessor.setSessionId(sessionId);
//        }
//        headerAccessor.setLeaveMutable(true);
//        return headerAccessor.getMessageHeaders();
//    }
}
