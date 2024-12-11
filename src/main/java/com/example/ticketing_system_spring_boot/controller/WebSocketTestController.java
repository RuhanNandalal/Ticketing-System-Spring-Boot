package com.example.ticketing_system_spring_boot.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketTestController {

    @MessageMapping("/test") // Maps to /app/test
    @SendTo("/topic/response") // Sends response to /topic/response
    public String testMessage(String message) {
        return "Server received: " + message;
    }
}

