package com.example.slackintegration.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SlackNotificationTest {

    private Validator validator;
    private SlackNotification notification;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // Create a valid notification
        notification = new SlackNotification("#general", "Test message");
        notification.setUsername("Test Bot");
        notification.setIconEmoji(":robot_face:");
    }

    @Test
    void validNotification() {
        // Act
        Set<ConstraintViolation<SlackNotification>> violations = validator.validate(notification);
        
        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullChannel() {
        // Arrange
        notification.setChannel(null);
        
        // Act
        Set<ConstraintViolation<SlackNotification>> violations = validator.validate(notification);
        
        // Assert
        assertEquals(1, violations.size());
        assertEquals("Channel is required", violations.iterator().next().getMessage());
    }

    @Test
    void emptyChannel() {
        // Arrange
        notification.setChannel("");
        
        // Act
        Set<ConstraintViolation<SlackNotification>> violations = validator.validate(notification);
        
        // Assert
        assertEquals(1, violations.size());
        assertEquals("Channel is required", violations.iterator().next().getMessage());
    }

    @Test
    void nullMessage() {
        // Arrange
        notification.setMessage(null);
        
        // Act
        Set<ConstraintViolation<SlackNotification>> violations = validator.validate(notification);
        
        // Assert
        assertEquals(1, violations.size());
        assertEquals("Message is required", violations.iterator().next().getMessage());
    }

    @Test
    void emptyMessage() {
        // Arrange
        notification.setMessage("");
        
        // Act
        Set<ConstraintViolation<SlackNotification>> violations = validator.validate(notification);
        
        // Assert
        assertEquals(1, violations.size());
        assertEquals("Message is required", violations.iterator().next().getMessage());
    }

    @Test
    void constructorAndGetters() {
        // Arrange & Act
        SlackNotification notification = new SlackNotification("#channel", "Message");
        
        // Assert
        assertEquals("#channel", notification.getChannel());
        assertEquals("Message", notification.getMessage());
        assertFalse(notification.isSent());
        assertNull(notification.getUsername());
        assertNull(notification.getIconEmoji());
        assertNull(notification.getSentAt());
        assertNull(notification.getResponseMessage());
    }

    @Test
    void settersAndGetters() {
        // Arrange
        SlackNotification notification = new SlackNotification();
        LocalDateTime now = LocalDateTime.now();
        
        // Act
        notification.setId(1L);
        notification.setChannel("#channel");
        notification.setMessage("Message");
        notification.setUsername("Username");
        notification.setIconEmoji(":smile:");
        notification.setSent(true);
        notification.setSentAt(now);
        notification.setResponseMessage("Success");
        
        // Assert
        assertEquals(1L, notification.getId());
        assertEquals("#channel", notification.getChannel());
        assertEquals("Message", notification.getMessage());
        assertEquals("Username", notification.getUsername());
        assertEquals(":smile:", notification.getIconEmoji());
        assertTrue(notification.isSent());
        assertEquals(now, notification.getSentAt());
        assertEquals("Success", notification.getResponseMessage());
    }

    @Test
    void defaultConstructor() {
        // Act
        SlackNotification notification = new SlackNotification();
        
        // Assert
        assertNull(notification.getChannel());
        assertNull(notification.getMessage());
        assertFalse(notification.isSent());
    }
} 