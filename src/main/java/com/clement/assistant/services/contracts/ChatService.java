package com.clement.assistant.services.contracts;

import com.clement.assistant.dtos.Answer;
import com.clement.assistant.dtos.Question;

public interface ChatService {
    Answer askQuestion(Question question);
}
