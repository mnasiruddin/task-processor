package com.coolplanet.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Represents the configuration properties for R2DBC (Reactive Relational Database Connectivity).
 *
 * This record is used to bind properties prefixed with "spring.r2dbc" from application properties files.
 * It encapsulates essential database connection details, including the following:
 * - Database URL
 * - Username for authentication
 * - Password for authentication
 *
 * Annotations:
 * - {@link ConfigurationProperties}: Indicates that this class can be used to bind external configuration
 *   properties to its fields. The prefix "spring.r2dbc" specifies the property namespace.
 *
 * This configuration is typically utilized to provide connection details to a {@link ConnectionFactory}
 * for establishing reactive database connections.
 */
@ConfigurationProperties(prefix = "spring.r2dbc")
public record R2dbcPropertiesConfig(String url, String username, String password) {
}
