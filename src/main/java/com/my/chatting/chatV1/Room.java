package com.my.chatting.chatV1;

import jakarta.websocket.Session;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class Room {
    private String id;
    private Set<Session> sessions = new HashSet<>();

    public void setSession(Session session){
        sessions.add(session);
    }
}
