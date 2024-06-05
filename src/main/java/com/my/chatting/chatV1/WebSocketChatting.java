package com.my.chatting.chatV1;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//WebSocketChatting 클래스를 서비스(WebSocket)로 등록
@Slf4j
@Service
@ServerEndpoint(value = "/chat/{roomId}/{user}")
public class WebSocketChatting {
    //    private static Set<Session> CLIENTS = Collections.synchronizedSet(new HashSet<>());
//    private static Set<Room> ROOM = Collections.synchronizedSet(new HashSet<>());
    private static Map<String, Room> CHATMAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId, @PathParam("user") String user) throws IOException {
        log.info("session {}", session.toString());

        if (CHATMAP.containsKey(roomId)) {
            Room room = CHATMAP.get(roomId);
            if (room.getSessions().size() >= 1) {
                room.getSessions().iterator().next().getBasicRemote().sendText(user + "님이 입장했습니다. 반갑게 인사해주세요.");
            }
            room.setSession(session);
        } else {
            Room room = new Room();
            room.setId(roomId);
            room.setSession(session);
            CHATMAP.put(roomId, room);
        }
        session.getBasicRemote().sendText(user + "님 환영합니다.");

//        ROOM.add(new Room(roomId, user));
//        Long roomId = System.currentTimeMillis();

//        if(ROOM.)

//        if(CLIENTS.contains(session)) {
//            log.info("SYSTEM 이미 연결된 세션 -> {}", session);
//        }else {
//            CLIENTS.add(session);
//            log.info("SYSTEM 새로운 세션 -> {}", session);
//        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") String roomId, @PathParam("user") String user) throws IOException {
//        CLIENTS.remove(session);
        Room room = CHATMAP.get(roomId);
        room.getSessions().remove(session);
        log.info("SYSTEM 세션을 닫음 -> {}", session);

        if (room.getSessions().size() == 0) {
//            Set<Session> sessions = room.getSessions();
            CHATMAP.remove(roomId);
        } else {
            room.getSessions().iterator().next().getBasicRemote().sendText(user + "님이 떠나갔습니다... 다음 분을 기다리세요...");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("roomId") String roomId) throws IOException {
        log.info("SYSTEM 입력된 메시지 -> {}", message);

//        for(Session client : CLIENTS) {
//            log.info("SYSTEM 메시지 전달 -> {} -> {}", message, session);
//            client.getBasicRemote().sendText(message);
//        }

        Room room = CHATMAP.get(roomId);
        for (Session client : room.getSessions()) {
            client.getBasicRemote().sendText(message);
        }
    }

    public String getRoomId() {
//        ROOM.stream().filter(room -> {
//            if(room.getUser1() == null || room.getUser2() == null){
//                return room.getId();
//            }
//        });

//        Iterator<Room> iterator = ROOM.iterator();
//        while (ROOM.iterator().hasNext()) {
//            Room room = ROOM.iterator().next();
//            if(room.getUser1()==null || room.getUser2()==null){
//                return room.getId();
//            }
//        }
        for (String key : CHATMAP.keySet()) {
            Room room = CHATMAP.get(key);
            if (room.getSessions().size() < 2) {
                return key;
            }
        }
        return null;
    }
}
