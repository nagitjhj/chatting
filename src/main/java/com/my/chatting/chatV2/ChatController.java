package com.my.chatting.chatV2;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate; // = new AbstractMessageSendingTemplate<>()
    //Stomp에 연결된 세션 사용자 장보를 가져오는 것 -> 안됨 ㅡㅡ
    private final SimpUserRegistry simpUserRegistry;

    @MessageMapping("/star")
    public void processMessage(ChatMessage message, SimpMessageHeaderAccessor accessor) {
        Set<SimpUser> users = simpUserRegistry.getUsers();
        for (SimpUser user : users) {
            log.info(user.getName());
        }

        SimpUser user = simpUserRegistry.getUser(message.getSender());
        if(user != null)
            log.info(user.getName());

        messagingTemplate.convertAndSend("/topic/star", message); //여기서 처리함
        //@SendTo나 messagingTemplated으로 메시지를 클라이언트로 전달
    }

    @MessageMapping("/sendToUser")
    public void userToSend(ChatMessage message, SimpMessageHeaderAccessor accessor){
        messagingTemplate.convertAndSendToUser(message.getSender(), "/queue/reply", message, createHeaders(message.getSender()));
        messagingTemplate.convertAndSendToUser(accessor.getSessionId(), "/queue/reply", message, createHeaders(accessor.getSessionId()));
    }

    @GetMapping("/chatting")
    public String chatting(Model model){
        return "chatV2/chatV2";
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
