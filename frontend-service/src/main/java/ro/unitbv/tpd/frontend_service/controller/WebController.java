package ro.unitbv.tpd.frontend_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "dashboard"; // Va căuta dashboard.html
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat"; // Va căuta chat.html
    }
}
