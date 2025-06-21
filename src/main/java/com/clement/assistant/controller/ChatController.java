package com.clement.assistant.controller;

import com.clement.assistant.dtos.Answer;
import com.clement.assistant.dtos.Question;
import com.clement.assistant.services.contracts.ChatService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(@Qualifier("questionService") ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Answer askQuestion(@RequestBody Question question) {
        return chatService.askQuestion(question);
    }
}
