package com.example.perftest;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

/**
 * The K6Runner class provides functionality to execute a K6 performance testing script
 * using a Docker container. It utilizes the Testcontainers library to manage the
 * lifecycle of the container.
 *
 * This class copies a K6 JavaScript test script to the container, executes it, and waits
 * for the completion of the script execution. The results of the script execution
 * are exported to a JSON file within the container.
 *
 * Key Features:
 * - Automatically starts and stops the container during the test execution.
 * - Allows mounting of a test script from classpath resources to the container.
 * - Configures the command to be executed within the container.
 *
 * Usage of Thread.Sleep provides a waiting mechanism to allow the K6 test to complete.
 * Any exceptions encountered during execution will wrap into a RuntimeException.
 */
public class K6Runner {

    public static void main(String[] args) {
        MountableFile script = MountableFile.forClasspathResource("task-test.js");

        try (GenericContainer<?> k6 = new GenericContainer<>("grafana/k6:latest")
                .withCopyFileToContainer(script, "/test.js")
                .withCommand("run", "--summary-export=/tmp/results.json", "/test.js")) {

            k6.start();
            System.out.println("K6 started");
            System.out.println("Sleeping for 100 seconds to complete the test");
            Thread.sleep(100000); // sleeping for 100 seconds to let the k6 run finish
            k6.stop();
            System.out.println("K6 stopped");
        } catch (Exception exception) {
            throw new RuntimeException("Failed to run k6", exception);
        }
    }
}