package com.my.chatting.chatV3.chattingRoom.controller;

import com.my.chatting.chatV3.chattingRoom.dto.ChattingRoom;
import com.my.chatting.chatV3.chattingRoom.dto.request.RequestCreateRoom;
import com.my.chatting.chatV3.chattingRoom.repository.ChattingRoomRepository;
import com.my.chatting.chatV3.chattingRoom.service.ChattingRoomService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/chatV3/chatting-room")
@RequiredArgsConstructor
public class ChattingRoomController {
    private final ChattingRoomService chattingRoomService;
    private final ChattingRoomRepository repository;
    private final SimpMessagingTemplate template;

    @GetMapping("/create-room")
    public String room() {
        return "/chatV3/chatting-room/create-room";
    }

    @PostMapping("/room")
    public String createRoom(RequestCreateRoom room, Model model) {
        ChattingRoom chattingRoom = chattingRoomService.createChattingRoom(room);
        model.addAttribute("room", chattingRoom);
        return "chatV3/chatting-room/room";
    }

    @DeleteMapping("/room")
    public String deleteRoom(@RequestBody Map<String, String> map) {
        ChattingRoom roomId = repository.findById(map.get("roomId"));
//        template.convertAndSend("/room/delete/"+map.get("roomId"), map.get("roomId"));
        repository.delete(map.get("roomId"));
        template.convertAndSend("/room/room-list/delete-room", map.get("roomId"));

        roomId.getMemberList().forEach(room -> {
            if(!room.isHost()){
                template.convertAndSendToUser(room.getId(),"/queue/room/delete", map.get("roomId"), createHeaders(room.getId()));
            }
        });

        return "chatV3/chatting-room/room-list";
    }

    @PostMapping("/room/{roomId}")
    public String createRoom(@PathVariable("roomId") String roomId, Model model) {
        ChattingRoom room = repository.findById(roomId);
        model.addAttribute("room", room);
        return "chatV3/chatting-room/room";
    }

    @PostMapping("/room/room")
    public String createRoomV2(String roomId, Model model) {
        ChattingRoom room = repository.findById(roomId);
        model.addAttribute("room", room);
        return "chatV3/chatting-room/room";
    }

    @ResponseBody
    @PostMapping("/room/pw")
    public ResponseEntity<String> enterPrivateRoom(@RequestBody Map<String, String> map) {
        ChattingRoom room = repository.findById(map.get("roomId"));

        if(room.getPw().equals(map.get("pw"))){
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/room-list")
    public String roomList(Model model) {
        Map<String, ChattingRoom> all = repository.findAll();
//        model.addAttribute("roomMap", all);
        return "chatV3/chatting-room/room-list";
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
