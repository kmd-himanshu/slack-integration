package com.example.slackintegration.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            // Execute a simple query to check database connectivity
            int result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result == 1) {
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connected")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Unexpected response")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "Not connected")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
} 