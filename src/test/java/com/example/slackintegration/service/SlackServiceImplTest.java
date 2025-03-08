package com.example.slackintegration.service;

import com.example.slackintegration.model.SlackNotification;
import com.example.slackintegration.repository.SlackNotificationRepository;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlackServiceImplTest {

    @Mock
    private SlackNotificationRepository notificationRepository;

    @Mock
    private MethodsClient slackClient;

    @InjectMocks
    private SlackServiceImpl slackService;

    private SlackNotification testNotification;
    private ChatPostMessageResponse successResponse;
    private ChatPostMessageResponse failureResponse;

    @BeforeEach
    void setUp() {
        // Set up test notification
        testNotification = new SlackNotification("#test-channel", "Test message");
        testNotification.setUsername("Test Bot");
        testNotification.setIconEmoji(":robot_face:");

        // Set up mock responses
        successResponse = new ChatPostMessageResponse();
        successResponse.setOk(true);

        failureResponse = new ChatPostMessageResponse();
        failureResponse.setOk(false);
        failureResponse.setError("channel_not_found");
    }

    @Test
    void sendMessage_Success() throws IOException, SlackApiException {
        // Arrange
        when(slackClient.chatPostMessage(any(ChatPostMessageRequest.class))).thenReturn(successResponse);
        when(notificationRepository.save(any(SlackNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SlackNotification result = slackService.sendMessage(testNotification);

        // Assert
        assertTrue(result.isSent());
        assertEquals("Success", result.getResponseMessage());
        assertNotNull(result.getSentAt());
        
        // Verify interactions
        verify(slackClient).chatPostMessage(any(ChatPostMessageRequest.class));
        verify(notificationRepository).save(testNotification);
        
        // Verify request builder contains correct values
        ArgumentCaptor<ChatPostMessageRequest> requestCaptor = ArgumentCaptor.forClass(ChatPostMessageRequest.class);
        verify(slackClient).chatPostMessage(requestCaptor.capture());
        ChatPostMessageRequest capturedRequest = requestCaptor.getValue();
        
        assertEquals("#test-channel", capturedRequest.getChannel());
        assertEquals("Test message", capturedRequest.getText());
        assertEquals("Test Bot", capturedRequest.getUsername());
        assertEquals(":robot_face:", capturedRequest.getIconEmoji());
    }

    @Test
    void sendMessage_Failure() throws IOException, SlackApiException {
        // Arrange
        when(slackClient.chatPostMessage(any(ChatPostMessageRequest.class))).thenReturn(failureResponse);
        when(notificationRepository.save(any(SlackNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SlackNotification result = slackService.sendMessage(testNotification);

        // Assert
        assertFalse(result.isSent());
        assertEquals("channel_not_found", result.getResponseMessage());
        assertNotNull(result.getSentAt());
        
        // Verify interactions
        verify(slackClient).chatPostMessage(any(ChatPostMessageRequest.class));
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void sendMessage_Exception() throws IOException, SlackApiException {
        // Arrange
        when(slackClient.chatPostMessage(any(ChatPostMessageRequest.class)))
                .thenThrow(new IOException("Network error"));
        when(notificationRepository.save(any(SlackNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SlackNotification result = slackService.sendMessage(testNotification);

        // Assert
        assertFalse(result.isSent());
        assertTrue(result.getResponseMessage().contains("Error: Network error"));
        
        // Verify interactions
        verify(slackClient).chatPostMessage(any(ChatPostMessageRequest.class));
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void getAllNotifications() {
        // Arrange
        List<SlackNotification> expectedNotifications = Arrays.asList(
                new SlackNotification("#channel1", "Message 1"),
                new SlackNotification("#channel2", "Message 2")
        );
        when(notificationRepository.findAll()).thenReturn(expectedNotifications);

        // Act
        List<SlackNotification> result = slackService.getAllNotifications();

        // Assert
        assertEquals(expectedNotifications, result);
        assertEquals(2, result.size());
        
        // Verify interactions
        verify(notificationRepository).findAll();
    }

    @Test
    void getNotificationById_Found() {
        // Arrange
        Long id = 1L;
        SlackNotification expectedNotification = new SlackNotification("#channel", "Message");
        when(notificationRepository.findById(id)).thenReturn(Optional.of(expectedNotification));

        // Act
        SlackNotification result = slackService.getNotificationById(id);

        // Assert
        assertEquals(expectedNotification, result);
        
        // Verify interactions
        verify(notificationRepository).findById(id);
    }

    @Test
    void getNotificationById_NotFound() {
        // Arrange
        Long id = 999L;
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        SlackNotification result = slackService.getNotificationById(id);

        // Assert
        assertNull(result);
        
        // Verify interactions
        verify(notificationRepository).findById(id);
    }

    @Test
    void saveNotification() {
        // Arrange
        SlackNotification notificationToSave = new SlackNotification("#channel", "Message");
        when(notificationRepository.save(notificationToSave)).thenReturn(notificationToSave);

        // Act
        SlackNotification result = slackService.saveNotification(notificationToSave);

        // Assert
        assertEquals(notificationToSave, result);
        
        // Verify interactions
        verify(notificationRepository).save(notificationToSave);
    }

    @Test
    void deleteNotification() {
        // Arrange
        Long id = 1L;
        doNothing().when(notificationRepository).deleteById(id);

        // Act
        slackService.deleteNotification(id);

        // Verify interactions
        verify(notificationRepository).deleteById(id);
    }
} 