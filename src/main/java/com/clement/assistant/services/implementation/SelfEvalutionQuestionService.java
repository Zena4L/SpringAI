package com.clement.assistant.services.implementation;

import com.clement.assistant.dtos.Answer;
import com.clement.assistant.dtos.Question;
import com.clement.assistant.exception.AnswerNotRelevantException;
import com.clement.assistant.services.contracts.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("selfEvaluationQuestionService")
public class SelfEvalutionQuestionService implements ChatService {
    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;

    public SelfEvalutionQuestionService(ChatClient.Builder chatClientBulder) {
        this.chatClient = chatClientBulder.build();
        this.evaluator = new RelevancyEvaluator(chatClientBulder);
    }

    @Retryable(retryFor = AnswerNotRelevantException.class)
    @Override
    public Answer askQuestion(Question question) {
        String answerText = chatClient.prompt()
            .user(question.question())
            .call().content();

        evaluateRelevancy(question, answerText);

        return new Answer(question.title(), answerText);
    }

    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        return new Answer("Title","I'm sorry, but the answer provided was not relevant. " +
            "Please try asking the question again or rephrase it for better clarity.");
    }

    private void evaluateRelevancy(Question question, String answerText) {
        EvaluationRequest evaluationRequest = new EvaluationRequest(
            question.question(),
            List.of(),
            answerText
        );

        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
        if (!evaluationResponse.isPass()) {
            throw new AnswerNotRelevantException(question.question(), answerText);
        }
    }
}
