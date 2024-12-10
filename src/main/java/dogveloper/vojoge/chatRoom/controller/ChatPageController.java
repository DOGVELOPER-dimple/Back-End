package dogveloper.vojoge.chatRoom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatPageController {

    @GetMapping("/chatPage")
    public String chatPageView(){
        return "index";
    }
}
