
package za.co.pulseops.controller;

import za.co.pulseops.model.KpiRecord;
import za.co.pulseops.service.ScoringService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import java.util.*;

@RestController
@RequestMapping("/api/scoring")
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/analyse")
    public ResponseEntity<?> analyse(
        @Valid @RequestBody List<KpiRecord> records
    ) {

        try {

            if (records == null || records.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "At least one KPI is required.")
                );
            }

            List<Map<String, Object>> scoredRecords =
                scoringService.scoreRecords(records);

            double overallScore =
                scoringService.calculateOverallScore(records);

            List<Map<String, Object>> departments =
                scoringService.departmentScores(scoredRecords);

            List<Map<String, Object>> priorities =
                scoringService.findPriorities(scoredRecords);

            long belowTarget = scoredRecords.stream()
                .filter(record ->
                    (Double) record.get("score") < 80
                )
                .count();

            Map<String, Object> response =
                new LinkedHashMap<>();

            response.put("overallScore", overallScore);
            response.put("kpiCount", records.size());
            response.put("belowCount", belowTarget);
            response.put("departmentCount", departments.size());
            response.put("departments", departments);
            response.put("priorities", priorities);
            response.put("records", scoredRecords);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException error) {

            return ResponseEntity.badRequest().body(
                Map.of("error", error.getMessage())
            );
        }
    }
}
