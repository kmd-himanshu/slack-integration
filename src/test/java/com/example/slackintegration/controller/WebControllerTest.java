package com.example.slackintegration.controller;

import com.example.slackintegration.model.SlackNotification;
import com.example.slackintegration.service.SlackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SlackService slackService;

    private SlackNotification testNotification;
    private List<SlackNotification> notificationList;

    @BeforeEach
    void setUp() {
        // Set up test notification
        testNotification = new SlackNotification("#test-channel", "Test message");
        testNotification.setId(1L);
        testNotification.setUsername("Test Bot");
        testNotification.setIconEmoji(":robot_face:");
        testNotification.setSent(true);
        testNotification.setSentAt(LocalDateTime.now());
        testNotification.setResponseMessage("Success");

        // Set up notification list
        notificationList = Arrays.asList(
                testNotification,
                new SlackNotification("#channel2", "Message 2")
        );
        notificationList.get(1).setId(2L);
    }

    @Test
    void home_WithNotifications() throws Exception {
        // Arrange
        when(slackService.getAllNotifications()).thenReturn(notificationList);

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("notification"))
                .andExpect(model().attributeExists("notifications"))
                .andExpect(model().attribute("notifications", notificationList));

        // Verify
        verify(slackService).getAllNotifications();
    }

    @Test
    void home_WithoutNotifications() throws Exception {
        // Arrange
        when(slackService.getAllNotifications()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("notification"))
                .andExpect(model().attributeExists("notifications"))
                .andExpect(model().attribute("notifications", Collections.emptyList()));

        // Verify
        verify(slackService).getAllNotifications();
    }

    @Test
    void sendMessage_Success() throws Exception {
        // Arrange
        when(slackService.sendMessage(any(SlackNotification.class))).thenReturn(testNotification);

        // Act & Assert
        mockMvc.perform(post("/send")
                .param("channel", "#test-channel")
                .param("message", "Test message")
                .param("username", "Test Bot")
                .param("iconEmoji", ":robot_face:"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Verify
        verify(slackService).sendMessage(any(SlackNotification.class));
    }

    @Test
    void sendMessage_ValidationError() throws Exception {
        // Act & Assert - Empty message should cause validation error
        mockMvc.perform(post("/send")
                .param("channel", "#test-channel")
                .param("message", "")) // Empty message
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("notifications"));

        // Verify no service call was made
        verify(slackService, never()).sendMessage(any(SlackNotification.class));
        verify(slackService).getAllNotifications(); // This is called to populate the model
    }

    @Test
    void viewHistory() throws Exception {
        // Arrange
        when(slackService.getAllNotifications()).thenReturn(notificationList);

        // Act & Assert
        mockMvc.perform(get("/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("history"))
                .andExpect(model().attributeExists("notifications"))
                .andExpect(model().attribute("notifications", notificationList));

        // Verify
        verify(slackService).getAllNotifications();
    }
} 