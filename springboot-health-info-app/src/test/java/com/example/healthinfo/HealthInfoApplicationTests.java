package com.example.healthinfo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test to verify that the Spring application context loads successfully.
 * Uses an embedded MongoDB-less configuration to avoid requiring a running database.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/testdb",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
})
class HealthInfoApplicationTests {

    /**
     * Verifies that the application context starts without errors.
     */
    @Test
    void contextLoads() {
        // Context load test — passes if Spring context initializes successfully
    }
}
