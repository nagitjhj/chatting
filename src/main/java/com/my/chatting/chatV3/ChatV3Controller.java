package com.my.chatting.chatV3;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chatV3")
public class ChatV3Controller {
    @GetMapping("/main")
    public String main(){
        return "chatV3/main";
    }
    @GetMapping("/chattingRoom")
    public String chattingRoom(){
        return "chatV3/chattingRoom";
    }
    @GetMapping("/bubbleRoom")
    public String bubbleRoom(){
        return "chatV3/bubbleRoom";
    }
}
