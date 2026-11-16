
package za.co.pulseops.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {

        return Map.of(
            "application", "PulseOps",
            "status", "running",
            "message", "PulseOps backend is operational"
        );
    }

    @GetMapping("/api")
    public Map<String, String> home() {

        return Map.of(
            "name", "PulseOps API",
            "version", "1.0.0",
            "description", "Operational Analytics Platform"
        );
    }
}
