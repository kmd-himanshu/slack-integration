package com.example.slackintegration.controller;

import com.example.slackintegration.model.SlackNotification;
import com.example.slackintegration.service.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
public class WebController {

    private final SlackService slackService;

    @Autowired
    public WebController(SlackService slackService) {
        this.slackService = slackService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("notifications", slackService.getAllNotifications());
        model.addAttribute("notification", new SlackNotification());
        return "index";
    }

    @PostMapping("/send")
    public String sendMessage(@Valid @ModelAttribute("notification") SlackNotification notification,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("notifications", slackService.getAllNotifications());
            return "index";
        }
        
        slackService.sendMessage(notification);
        return "redirect:/";
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        model.addAttribute("notifications", slackService.getAllNotifications());
        return "history";
    }
} 