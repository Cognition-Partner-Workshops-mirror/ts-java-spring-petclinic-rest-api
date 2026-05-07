package com.example.healthinfo.service;

import com.example.healthinfo.model.AppInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for InfoService.
 * Uses test properties to inject known values and validates the returned AppInfo.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "app.name=test-app",
    "app.version=1.0.0-TEST",
    "app.description=Test application description",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
})
class InfoServiceTest {

    @Autowired
    private InfoService infoService;

    /**
     * Verify that getAppInfo returns a non-null AppInfo object.
     */
    @Test
    void getAppInfo_shouldReturnNonNullInfo() {
        AppInfo info = infoService.getAppInfo();
        assertNotNull(info, "AppInfo should not be null");
    }

    /**
     * Verify that the application name matches the configured test value.
     */
    @Test
    void getAppInfo_shouldReturnConfiguredName() {
        AppInfo info = infoService.getAppInfo();
        assertEquals("test-app", info.getName(), "App name should match test property");
    }

    /**
     * Verify that the application version matches the configured test value.
     */
    @Test
    void getAppInfo_shouldReturnConfiguredVersion() {
        AppInfo info = infoService.getAppInfo();
        assertEquals("1.0.0-TEST", info.getVersion(), "App version should match test property");
    }

    /**
     * Verify that the application description matches the configured test value.
     */
    @Test
    void getAppInfo_shouldReturnConfiguredDescription() {
        AppInfo info = infoService.getAppInfo();
        assertEquals("Test application description", info.getDescription(),
            "App description should match test property");
    }
}
