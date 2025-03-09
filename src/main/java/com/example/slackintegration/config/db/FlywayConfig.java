package com.example.slackintegration.config.db;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Flyway configuration for database migrations.
 * Note: Spring Boot auto-configures Flyway by default, so this class is only needed
 * if you need custom configuration beyond what's in application.properties.
 */
@Configuration
public class FlywayConfig {

    @Autowired
    private Environment env;

    /**
     * Configure Flyway with custom settings.
     * This bean is only needed if you need configuration beyond what Spring Boot provides.
     */
    @Bean(initMethod = "migrate")
    @DependsOn("dataSource")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(env.getProperty("spring.flyway.locations", "classpath:db/migration"))
                .baselineOnMigrate(Boolean.parseBoolean(env.getProperty("spring.flyway.baseline-on-migrate", "true")))
                .baselineVersion(env.getProperty("spring.flyway.baseline-version", "0"))
                .load();
    }
} 