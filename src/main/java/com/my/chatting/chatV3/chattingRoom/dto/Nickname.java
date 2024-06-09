package com.my.chatting.chatV3.chattingRoom.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Nickname {
    광수
    , 영철
    , 영자
    , 옥순
    , 순자
    ;

    public static Set<Nickname> getRandomNickName() {
        Set<Nickname> nickname = new HashSet<>(Arrays.asList(Nickname.values()));
        return nickname;
    }
}
