
package za.co.pulseops.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import za.co.pulseops.model.KpiRecord;
import za.co.pulseops.service.AnalyticsClientService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsClientService analyticsClientService;

    public AnalyticsController(
            AnalyticsClientService analyticsClientService) {
        this.analyticsClientService = analyticsClientService;
    }

    @PostMapping("/analyse")
    public Map<String, Object> analyse(
            @Valid @RequestBody List<@Valid KpiRecord> records) {

        return analyticsClientService.analyse(records);
    }
}
