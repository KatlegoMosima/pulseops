
const ScoringEngine = {

    calculateKpiScore(record) {
        const actual = Number(record.actual);
        const target = Number(record.target);
        const weight = Number(record.weight);

        if (
            !Number.isFinite(actual) ||
            !Number.isFinite(target) ||
            !Number.isFinite(weight) ||
            target <= 0 ||
            weight <= 0
        ) {
            throw new Error(`Invalid values for KPI: ${record.kpi}`);
        }

        let score;

        if (record.direction.toLowerCase() === "higher") {
            score = Math.min(100, (actual / target) * 100);
        } 
        else if (record.direction.toLowerCase() === "lower") {
            score = actual <= target
                ? 100
                : Math.max(0, (target / actual) * 100);
        } 
        else {
            throw new Error(
                `Invalid direction for KPI: ${record.kpi}`
            );
        }

        return {
            ...record,
            score: Number(score.toFixed(2))
        };
    },

    calculateOverallScore(records) {
        const totalWeight = records.reduce(
            (sum, record) => sum + record.weight,
            0
        );

        if (totalWeight === 0) return 0;

        const weightedTotal = records.reduce(
            (sum, record) =>
                sum + record.score * record.weight,
            0
        );

        return Number(
            (weightedTotal / totalWeight).toFixed(2)
        );
    },

    calculateDepartments(records) {
        const departments = {};

        records.forEach(record => {
            if (!departments[record.department]) {
                departments[record.department] = {
                    total: 0,
                    count: 0
                };
            }

            departments[record.department].total += record.score;
            departments[record.department].count++;
        });

        return Object.entries(departments).map(
            ([department, data]) => ({
                department,
                score: Number(
                    (data.total / data.count).toFixed(2)
                )
            })
        );
    },

    findPriorityKpis(records) {
        return [...records]
            .sort((a, b) => a.score - b.score)
            .slice(0, 3);
    },

    generateRecommendations(records) {
        return this.findPriorityKpis(records).map(record => {
            let action;

            if (record.direction.toLowerCase() === "higher") {
                action = `Investigate why ${record.kpi} is below its target and introduce corrective actions to improve performance.`;
            } else {
                action = `Investigate the factors increasing ${record.kpi} and introduce controls to reduce it.`;
            }

            return {
                department: record.department,
                kpi: record.kpi,
                score: record.score,
                recommendation: action
            };
        });
    },

    analyse(records) {
        if (!Array.isArray(records) || records.length === 0) {
            throw new Error("No KPI records were provided.");
        }

        const scoredRecords = records.map(record =>
            this.calculateKpiScore(record)
        );

        return {
            overallScore: this.calculateOverallScore(scoredRecords),
            kpiCount: scoredRecords.length,
            belowCount: scoredRecords.filter(
                record => record.score < 80
            ).length,
            departments: this.calculateDepartments(scoredRecords),
            priorities: this.findPriorityKpis(scoredRecords),
            recommendations: this.generateRecommendations(scoredRecords),
            records: scoredRecords
        };
    }
};

