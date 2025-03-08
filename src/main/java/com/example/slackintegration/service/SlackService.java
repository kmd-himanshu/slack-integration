package com.example.slackintegration.service;

import com.example.slackintegration.model.SlackNotification;

import java.util.List;

public interface SlackService {
    
    /**
     * Send a message to a Slack channel
     * 
     * @param notification The notification to send
     * @return The updated notification with sent status
     */
    SlackNotification sendMessage(SlackNotification notification);
    
    /**
     * Get all notifications
     * 
     * @return List of all notifications
     */
    List<SlackNotification> getAllNotifications();
    
    /**
     * Get a notification by ID
     * 
     * @param id The notification ID
     * @return The notification if found
     */
    SlackNotification getNotificationById(Long id);
    
    /**
     * Save a notification without sending it
     * 
     * @param notification The notification to save
     * @return The saved notification
     */
    SlackNotification saveNotification(SlackNotification notification);
    
    /**
     * Delete a notification
     * 
     * @param id The notification ID to delete
     */
    void deleteNotification(Long id);
} 