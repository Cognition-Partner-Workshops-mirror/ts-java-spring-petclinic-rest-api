package com.example.healthinfo.model;

/**
 * Model representing application metadata.
 * Holds the application name, version, and a brief description.
 */
public class AppInfo {

    /** Name of the application */
    private String name;

    /** Current version of the application */
    private String version;

    /** Brief description of what the application does */
    private String description;

    public AppInfo() {
    }

    public AppInfo(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
