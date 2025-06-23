package com.clement.assistant.services.implementation;

import com.clement.assistant.dtos.Answer;
import com.clement.assistant.dtos.Question;
import com.clement.assistant.services.contracts.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@Qualifier("questionService")
public class QuestionService implements ChatService {
    private final ChatClient chatClient;
    private final RulesService rulesService;
    private final VectorStore vectorStore;
    @Value("classpath:promptTemplates/systemPromptTemplate.st")
    Resource promptTemplate;

    private static final Logger logger = Logger.getLogger(QuestionService.class.getName());

    public QuestionService(ChatClient.Builder chatClientBuilder, RulesService rulesService, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder
            .build();
        this.rulesService = rulesService;
        this.vectorStore = vectorStore;
    }

    @Override
    public Answer askQuestion(Question question) {
        String ideaRule = rulesService.stuffingRule(question.title());
        var responseEntity = chatClient.prompt()
            .system(systemSpec ->
                systemSpec.text(promptTemplate)
                    .param("title", question.title())
                    .param("rules", ideaRule)
            )
            .user(question.question())
//            .advisors(advisorSpec ->
//                advisorSpec.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY))
            .call()
            .responseEntity(Answer.class);
//        return new Answer(question.title(), answerText);

        ChatResponse response = responseEntity.response();
        ChatResponseMetadata metadata = response.getMetadata();

        logUsage(metadata.getUsage());

        return responseEntity.entity();
    }

    private void logUsage(Usage usage) {
        logger.info("Usage details: " +
            "Prompt tokens: " + usage.getPromptTokens() + ", " +
            "Completion tokens: " + usage.getCompletionTokens() + ", " +
            "Total tokens: " + usage.getTotalTokens()
        );
    }
}
