package com.example.slackintegration.service;

import com.example.slackintegration.model.SlackNotification;
import com.example.slackintegration.repository.SlackNotificationRepository;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SlackServiceImpl implements SlackService {

    private static final Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);

    private final SlackNotificationRepository notificationRepository;
    private final MethodsClient slackClient;

    @Autowired
    public SlackServiceImpl(SlackNotificationRepository notificationRepository, MethodsClient slackClient) {
        this.notificationRepository = notificationRepository;
        this.slackClient = slackClient;
    }

    @Override
    public SlackNotification sendMessage(SlackNotification notification) {
        try {
            // Build the Slack message request
            ChatPostMessageRequest.ChatPostMessageRequestBuilder requestBuilder = ChatPostMessageRequest.builder()
                    .channel(notification.getChannel())
                    .text(notification.getMessage());

            // Add optional parameters if they exist
            if (notification.getUsername() != null && !notification.getUsername().isEmpty()) {
                requestBuilder.username(notification.getUsername());
            }

            if (notification.getIconEmoji() != null && !notification.getIconEmoji().isEmpty()) {
                requestBuilder.iconEmoji(notification.getIconEmoji());
            }

            // Send the message to Slack
            ChatPostMessageResponse response = slackClient.chatPostMessage(requestBuilder.build());

            // Update the notification with the result
            notification.setSent(response.isOk());
            notification.setSentAt(LocalDateTime.now());
            notification.setResponseMessage(response.isOk() ? "Success" : response.getError());

            // Save the updated notification
            return notificationRepository.save(notification);
        } catch (SlackApiException | IOException e) {
            logger.error("Error sending message to Slack: {}", e.getMessage());
            notification.setSent(false);
            notification.setResponseMessage("Error: " + e.getMessage());
            return notificationRepository.save(notification);
        }
    }

    @Override
    public List<SlackNotification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public SlackNotification getNotificationById(Long id) {
        Optional<SlackNotification> notification = notificationRepository.findById(id);
        return notification.orElse(null);
    }

    @Override
    public SlackNotification saveNotification(SlackNotification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
} 