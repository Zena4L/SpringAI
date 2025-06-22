package com.clement.assistant.services.implementation;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RulesService {
    private static final Logger logger = Logger.getLogger(RulesService.class.getName());
    private final VectorStore vectorStore;

    public RulesService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String stuffingRule(String input) {
        try {
            String filename = String.format(
                "classpath:/stuffing/%s.txt",
                input.toLowerCase().replace(" ", "_")
            );
            return new DefaultResourceLoader()
                .getResource(filename)
                .getContentAsString(Charset.defaultCharset());
        } catch (IOException e) {
            logger.severe("Error loading rule file: " + e.getMessage());
            return "Rule not found or error loading rule.";
        }
    }

    public String getRoleForRag(String gameName, String question) {
        SearchRequest searchRequest = SearchRequest.builder()
            .query(question)
            .topK(6)
            .similarityThreshold(0.5)
            .filterExpression(new FilterExpressionBuilder().eq("gameName", normalizedGameTitle(gameName)).build())
            .build();


        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        if (similarDocs.isEmpty()) {
            return "No relevant information found for the question.";
        }

        return similarDocs.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
    }

    private String normalizedGameTitle(String gameTitle) {
        return gameTitle.toLowerCase().replace(" ", "_" ) ;
    }
}
