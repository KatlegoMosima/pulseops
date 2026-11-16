
package za.co.pulseops.service;

import za.co.pulseops.model.KpiRecord;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoringService {

    public double calculateKpiScore(KpiRecord record) {

        double actual = record.getActual();
        double target = record.getTarget();

        if (actual < 0 || target <= 0) {
            throw new IllegalArgumentException(
                "Actual cannot be negative and target must be positive."
            );
        }

        String direction = record.getDirection()
            .toLowerCase(Locale.ROOT);

        double score;

        if (direction.equals("higher")) {

            score = Math.min(
                100,
                (actual / target) * 100
            );

        } else if (direction.equals("lower")) {

            score = actual <= target
                ? 100
                : (target / actual) * 100;

        } else {

            throw new IllegalArgumentException(
                "Direction must be higher or lower."
            );
        }

        return Math.round(score * 100.0) / 100.0;
    }

    public double calculateOverallScore(
        List<KpiRecord> records
    ) {

        double weightedTotal = 0;
        double totalWeight = 0;

        for (KpiRecord record : records) {

            double score = calculateKpiScore(record);

            weightedTotal += score * record.getWeight();
            totalWeight += record.getWeight();
        }

        if (totalWeight == 0) {
            throw new IllegalArgumentException(
                "Total KPI weight cannot be zero."
            );
        }

        return Math.round(
            (weightedTotal / totalWeight) * 100.0
        ) / 100.0;
    }

    public List<Map<String, Object>> scoreRecords(
        List<KpiRecord> records
    ) {

        List<Map<String, Object>> results =
            new ArrayList<>();

        for (KpiRecord record : records) {

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("department", record.getDepartment());
            item.put("kpi", record.getKpi());
            item.put("actual", record.getActual());
            item.put("target", record.getTarget());
            item.put("weight", record.getWeight());
            item.put("direction", record.getDirection());
            item.put("score", calculateKpiScore(record));

            results.add(item);
        }

        return results;
    }

    public List<Map<String, Object>> departmentScores(
        List<Map<String, Object>> scoredRecords
    ) {

        Map<String, List<Double>> departments =
            new LinkedHashMap<>();

        for (Map<String, Object> record : scoredRecords) {

            String department =
                (String) record.get("department");

            Double score =
                (Double) record.get("score");

            departments.computeIfAbsent(
                department,
                key -> new ArrayList<>()
            ).add(score);
        }

        List<Map<String, Object>> results =
            new ArrayList<>();

        departments.forEach((department, scores) -> {

            double average = scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("department", department);
            item.put("averageScore",
                Math.round(average * 100.0) / 100.0);
            item.put("kpiCount", scores.size());

            results.add(item);
        });

        return results;
    }

    public List<Map<String, Object>> findPriorities(
        List<Map<String, Object>> scoredRecords
    ) {

        return scoredRecords.stream()
            .sorted(Comparator.comparingDouble(
                record -> (Double) record.get("score")
            ))
            .limit(3)
            .collect(Collectors.toList());
    }
}
