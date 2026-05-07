package com.example.healthinfo.service;

import com.example.healthinfo.model.AppInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for providing application metadata.
 * Reads application name, version, and description from configuration properties.
 */
@Service
public class InfoService {

    /** Application name injected from application.yml */
    @Value("${app.name}")
    private String appName;

    /** Application version injected from application.yml */
    @Value("${app.version}")
    private String appVersion;

    /** Application description injected from application.yml */
    @Value("${app.description}")
    private String appDescription;

    /**
     * Retrieves application metadata as an AppInfo object.
     *
     * @return AppInfo populated with name, version, and description from config
     */
    public AppInfo getAppInfo() {
        // Build and return application info from injected configuration values
        return new AppInfo(appName, appVersion, appDescription);
    }
}
