package com.clement.assistant.services.implementation;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

@Service
public class RulesService {
    private static final Logger logger = Logger.getLogger(RulesService.class.getName());

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
}
