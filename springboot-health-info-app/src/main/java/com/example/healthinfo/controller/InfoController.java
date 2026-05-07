package com.example.healthinfo.controller;

import com.example.healthinfo.model.AppInfo;
import com.example.healthinfo.service.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the application info endpoint.
 * Delegates metadata retrieval to InfoService.
 */
@RestController
@RequestMapping("/api")
public class InfoController {

    private final InfoService infoService;

    /**
     * Constructor injection of InfoService.
     *
     * @param infoService service providing application metadata
     */
    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    /**
     * GET /api/info — Returns application metadata (name, version, description).
     *
     * @return AppInfo object serialized as JSON
     */
    @GetMapping("/info")
    public AppInfo getInfo() {
        // Delegate to the info service to retrieve application metadata
        return infoService.getAppInfo();
    }
}
