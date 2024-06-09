package com.my.chatting.chatV3.chattingRoom.controller;

import com.my.chatting.chatV3.chattingRoom.dto.ChattingRoom;
import com.my.chatting.chatV3.chattingRoom.dto.RequestCreateRoom;
import com.my.chatting.chatV3.chattingRoom.repository.ChattingRoomRepository;
import com.my.chatting.chatV3.chattingRoom.service.ChattingRoomService;
import lombok.RequiredArgsConstructor;
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
        repository.delete(map.get("roomId"));
        template.convertAndSend("/room/delete/"+map.get("roomId"), map.get("roomId"));
        template.convertAndSend("/room/room-list/delete-room", map.get("roomId"));
        return "chatV3/chatting-room/room-list";
    }

    @PostMapping("/room/{roomId}")
    public String createRoom(@PathVariable("roomId") String roomId, Model model) {
        ChattingRoom room = repository.findById(roomId);
        model.addAttribute("room", room);
        return "chatV3/chatting-room/room";
    }

    @GetMapping("/room-list")
    public String roomList(Model model) {
        Map<String, ChattingRoom> all = repository.findAll();
//        model.addAttribute("roomMap", all);
        return "chatV3/chatting-room/room-list";
    }
}
