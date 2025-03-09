package com.example.slackintegration.controller;

import com.example.slackintegration.model.SlackNotification;
import com.example.slackintegration.service.SlackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SlackController.class)
class SlackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SlackService slackService;

    private SlackNotification testNotification;
    private SlackNotification sentNotification;
    private List<SlackNotification> notificationList;

    @BeforeEach
    void setUp() {
        // Set up test notification
        testNotification = new SlackNotification("#test-channel", "Test message");
        testNotification.setUsername("Test Bot");
        testNotification.setIconEmoji(":robot_face:");

        // Set up sent notification with additional fields
        sentNotification = new SlackNotification("#test-channel", "Test message");
        sentNotification.setId(1L);
        sentNotification.setUsername("Test Bot");
        sentNotification.setIconEmoji(":robot_face:");
        sentNotification.setSent(true);
        sentNotification.setSentAt(LocalDateTime.now());
        sentNotification.setResponseMessage("Success");

        // Set up notification list
        notificationList = Arrays.asList(
                sentNotification,
                new SlackNotification("#channel2", "Message 2")
        );
        notificationList.get(1).setId(2L);
    }

    @Test
    void sendMessage() throws Exception {
        // Arrange
        when(slackService.sendMessage(any(SlackNotification.class))).thenReturn(sentNotification);

        // Act & Assert
        mockMvc.perform(post("/api/slack/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNotification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.channel", is("#test-channel")))
                .andExpect(jsonPath("$.message", is("Test message")))
                .andExpect(jsonPath("$.username", is("Test Bot")))
                .andExpect(jsonPath("$.iconEmoji", is(":robot_face:")))
                .andExpect(jsonPath("$.sent", is(true)))
                .andExpect(jsonPath("$.responseMessage", is("Success")))
                .andExpect(jsonPath("$.sentAt", notNullValue()));

        // Verify
        verify(slackService).sendMessage(any(SlackNotification.class));
    }

    @Test
    void getAllNotifications() throws Exception {
        // Arrange
        when(slackService.getAllNotifications()).thenReturn(notificationList);

        // Act & Assert
        mockMvc.perform(get("/api/slack"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].channel", is("#test-channel")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].channel", is("#channel2")));

        // Verify
        verify(slackService).getAllNotifications();
    }

    @Test
    void getNotificationById_Found() throws Exception {
        // Arrange
        Long id = 1L;
        when(slackService.getNotificationById(id)).thenReturn(sentNotification);

        // Act & Assert
        mockMvc.perform(get("/api/slack/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.channel", is("#test-channel")))
                .andExpect(jsonPath("$.message", is("Test message")));

        // Verify
        verify(slackService).getNotificationById(id);
    }

    @Test
    void getNotificationById_NotFound() throws Exception {
        // Arrange
        Long id = 999L;
        when(slackService.getNotificationById(id)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/slack/{id}", id))
                .andExpect(status().isNotFound());

        // Verify
        verify(slackService).getNotificationById(id);
    }

    @Test
    void saveNotification() throws Exception {
        // Arrange
        when(slackService.saveNotification(any(SlackNotification.class))).thenReturn(sentNotification);

        // Act & Assert
        mockMvc.perform(post("/api/slack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNotification)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.channel", is("#test-channel")))
                .andExpect(jsonPath("$.message", is("Test message")));

        // Verify
        verify(slackService).saveNotification(any(SlackNotification.class));
    }

    @Test
    void deleteNotification() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(slackService).deleteNotification(id);

        // Act & Assert
        mockMvc.perform(delete("/api/slack/{id}", id))
                .andExpect(status().isNoContent());

        // Verify
        verify(slackService).deleteNotification(id);
    }

    @Test
    void sendMessage_ValidationError() throws Exception {
        // Create an invalid notification (missing required fields)
        SlackNotification invalidNotification = new SlackNotification();

        // Act & Assert
        mockMvc.perform(post("/api/slack/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNotification)))
                .andExpect(status().isBadRequest());

        // Verify no service call was made
        verify(slackService, never()).sendMessage(any(SlackNotification.class));
    }
} 