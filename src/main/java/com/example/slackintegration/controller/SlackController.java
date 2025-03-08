package com.example.slackintegration.controller;

import com.example.slackintegration.model.SlackNotification;
import com.example.slackintegration.service.SlackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slack")
public class SlackController {

    private final SlackService slackService;

    @Autowired
    public SlackController(SlackService slackService) {
        this.slackService = slackService;
    }

    @PostMapping("/send")
    public ResponseEntity<SlackNotification> sendMessage(@Valid @RequestBody SlackNotification notification) {
        SlackNotification sentNotification = slackService.sendMessage(notification);
        return new ResponseEntity<>(sentNotification, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SlackNotification>> getAllNotifications() {
        List<SlackNotification> notifications = slackService.getAllNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlackNotification> getNotificationById(@PathVariable Long id) {
        SlackNotification notification = slackService.getNotificationById(id);
        if (notification != null) {
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<SlackNotification> saveNotification(@Valid @RequestBody SlackNotification notification) {
        SlackNotification savedNotification = slackService.saveNotification(notification);
        return new ResponseEntity<>(savedNotification, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        slackService.deleteNotification(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 