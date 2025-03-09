package com.example.slackintegration;

import com.example.slackintegration.controller.SlackController;
import com.example.slackintegration.controller.WebController;
import com.example.slackintegration.service.SlackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SlackIntegrationApplicationTests {

    @Autowired
    private SlackController slackController;

    @Autowired
    private WebController webController;

    @Autowired
    private SlackService slackService;

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
        assertNotNull(slackController);
        assertNotNull(webController);
        assertNotNull(slackService);
    }
} 