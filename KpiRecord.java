
package za.co.pulseops.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class KpiRecord {

    @NotBlank
    private String department;

    @NotBlank
    private String kpi;

    @NotNull
    private Double actual;

    @NotNull
    @Positive
    private Double target;

    @NotNull
    @Positive
    private Double weight;

    @NotBlank
    private String direction;

    public KpiRecord() {
    }

    public KpiRecord(
        String department,
        String kpi,
        Double actual,
        Double target,
        Double weight,
        String direction
    ) {
        this.department = department;
        this.kpi = kpi;
        this.actual = actual;
        this.target = target;
        this.weight = weight;
        this.direction = direction;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
    }

    public Double getActual() {
        return actual;
    }

    public void setActual(Double actual) {
        this.actual = actual;
    }

    public Double getTarget() {
        return target;
    }

    public void setTarget(Double target) {
        this.target = target;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
