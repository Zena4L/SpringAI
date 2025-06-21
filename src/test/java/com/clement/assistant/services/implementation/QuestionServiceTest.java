package com.clement.assistant.services.implementation;

import com.clement.assistant.dtos.Answer;
import com.clement.assistant.dtos.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private RelevancyEvaluator relevancyEvaluator;

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    void setUp() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);
    }

    @Test
    void testEvaluateQuestionRelevancy() {
        String userText = "Why is the sky blue?";

        Question question = new Question(userText);
        Answer answer = questionService.askQuestion(question);

        EvaluationRequest request = new EvaluationRequest(userText, answer.answer());

        EvaluationResponse response = relevancyEvaluator.evaluate(request);

        assertThat(response.isPass()).withFailMessage(
            """
                The answer %s to the question "%s" did not pass the relevancy evaluation.
                """, answer.answer(), userText
        ).isTrue();
    }

    @Test
    @Disabled
    void testEvaluateQuestionFactChecking() {
        String userText = "Why is the sky blue?";

        Question question = new Question(userText);
        Answer answer = questionService.askQuestion(question);

        EvaluationRequest request = new EvaluationRequest(userText, answer.answer());

        EvaluationResponse response = factCheckingEvaluator.evaluate(request);

        assertThat(response.isPass()).withFailMessage(
            """
                The answer %s to the question "%s" is not considered factually correct.
                """, answer.answer(), userText
        ).isTrue();
    }

}