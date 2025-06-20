package com.coolplanet.task.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "features.task")
public class FeatureConfig {

    /**
     * When true, tasks will be sent to Kafka (async).
     * When false, tasks will be saved directly to DB (sync).
     */
    private boolean asyncEnabled;
}

