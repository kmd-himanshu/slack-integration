package com.example.slackintegration.repository;

import com.example.slackintegration.model.SlackNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlackNotificationRepository extends JpaRepository<SlackNotification, Long> {
    
    List<SlackNotification> findBySent(boolean sent);
    
    List<SlackNotification> findByChannel(String channel);
} 