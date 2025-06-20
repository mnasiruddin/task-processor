
package com.coolplanet.task.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Configuration class for setting up R2DBC (Reactive Relational Database Connectivity) properties
 * and enabling R2DBC repositories in the application.
 *
 * This configuration leverages {@link R2dbcPropertiesConfig} for database connection details,
 * including URL, username, and password, which are then used to create a {@link ConnectionFactory}.
 *
 * An instance of {@link ConnectionFactory} is defined as a Spring Bean to manage database connections
 * in a reactive programming model.
 *
 * Annotations:
 * - {@link Configuration}: Marks this class as a configuration class in Spring's context.
 * - {@link EnableConfigurationProperties}: Makes the specified configuration properties class
 *   ({@link R2dbcPropertiesConfig}) available for dependency injection.
 * - {@link EnableR2dbcRepositories}: Enables R2DBC repository support, allowing the application
 *   to interact with the database in a reactive manner.
 */
@Configuration
@EnableConfigurationProperties(R2dbcPropertiesConfig.class)
@EnableR2dbcRepositories
public class R2DBCConfig {

    @Bean
    public ConnectionFactory connectionFactory(R2dbcPropertiesConfig props) {
        return ConnectionFactoryBuilder.withUrl(props.url())
                .username(props.username())
                .password(props.password())
                .build();
    }

}

