package com.clement.assistant.exception;

public class AnswerNotRelevantException extends RuntimeException {
    public AnswerNotRelevantException(String question, String answer) {
        super("This answer is not relevant to the question: '" + question + "'. Answer provided: '" + answer + "'.");
    }

}
