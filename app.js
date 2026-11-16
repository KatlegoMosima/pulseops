
const sampleData = [
    {
        department: "Sales",
        kpi: "Vehicle Sales",
        actual: 85,
        target: 100,
        weight: 3,
        direction: "higher"
    },
    {
        department: "Operations",
        kpi: "Vehicle Processing Time",
        actual: 8,
        target: 5,
        weight: 2,
        direction: "lower"
    },
    {
        department: "Finance",
        kpi: "Payment Collection",
        actual: 92,
        target: 100,
        weight: 3,
        direction: "higher"
    },
    {
        department: "Customer Service",
        kpi: "Customer Satisfaction",
        actual: 75,
        target: 90,
        weight: 2,
        direction: "higher"
    },
    {
        department: "Operations",
        kpi: "Stock Accuracy",
        actual: 96,
        target: 98,
        weight: 2,
        direction: "higher"
    }
];

function parseCSV(text) {
    const lines = text.trim().split(/\r?\n/);

    if (lines.length < 2) {
        throw new Error("The CSV file contains no data rows.");
    }

    const headers = lines[0]
        .split(",")
        .map(header => header.trim().toLowerCase());

    const required = [
        "department",
        "kpi",
        "actual",
        "target",
        "weight",
        "direction"
    ];

    const missing = required.filter(
        header => !headers.includes(header)
    );

    if (missing.length > 0) {
        throw new Error(
            "Missing CSV columns: " + missing.join(", ")
        );
    }

    return lines.slice(1).map((line, index) => {
        const values = line.split(",");
        const record = {};

        headers.forEach((header, i) => {
            record[header] = (values[i] || "").trim();
        });

        if (
            !record.department ||
            !record.kpi ||
            !record.actual ||
            !record.target ||
            !record.weight ||
            !record.direction
        ) {
            throw new Error(
                `Invalid or incomplete data on row ${index + 2}.`
            );
        }

        return {
            department: record.department,
            kpi: record.kpi,
            actual: Number(record.actual),
            target: Number(record.target),
            weight: Number(record.weight),
            direction: record.direction.toLowerCase()
        };
    });
}

function showResults(results) {
    document.getElementById("overallScore").textContent =
        results.overallScore + "/100";

    document.getElementById("kpiCount").textContent =
        results.kpiCount;

    document.getElementById("belowCount").textContent =
        results.belowCount;

    document.getElementById("deptCount").textContent =
        results.departments.length;

    // Department performance
    const departmentChart =
        document.getElementById("departmentChart");

    const ctx = departmentChart.getContext("2d");

    ctx.clearRect(
        0,
        0,
        departmentChart.width,
        departmentChart.height
    );

    const barHeight = 25;
    const gap = 20;
    const labelWidth = 130;
    const chartWidth = departmentChart.width - labelWidth - 35;

    results.departments.forEach((department, index) => {
        const y = 15 + index * (barHeight + gap);
        const barWidth = chartWidth * department.score / 100;

        ctx.fillStyle = "#263d62";
        ctx.font = "13px Arial";
        ctx.fillText(department.department, 5, y + 17);

        ctx.fillStyle = "#e5e9f0";
        ctx.fillRect(labelWidth, y, chartWidth, barHeight);

        ctx.fillStyle = "#3678d5";
        ctx.fillRect(labelWidth, y, barWidth, barHeight);

        ctx.fillStyle = "#172033";
        ctx.fillText(
            department.score + "%",
            labelWidth + chartWidth + 5,
            y + 17
        );
    });

    departmentChart.height = Math.max(
        180,
        results.departments.length * (barHeight + gap) + 15
    );

    // Priority KPIs
    const priorityList =
        document.getElementById("priorityList");

    priorityList.replaceChildren();

    results.priorities.forEach(item => {
        const li = document.createElement("li");

        li.textContent =
            `${item.kpi} (${item.department}) — ${item.score}/100`;

        priorityList.appendChild(li);
    });

    // KPI table
    const table = document.getElementById("kpiTable");
    table.replaceChildren();

    results.records.forEach(item => {
        const row = document.createElement("tr");

        const values = [
            item.department,
            item.kpi,
            item.actual,
            item.target,
            item.weight,
            item.score + "/100"
        ];

        values.forEach((value, index) => {
            const cell = document.createElement("td");
            cell.textContent = value;

            if (index === 5) {
                cell.className = "score " +
                    (item.score >= 80 ? "good" :
                    item.score >= 60 ? "warning" : "bad");
            }

            row.appendChild(cell);
        });

        table.appendChild(row);
    });

    // Recommendations
    const recommendationList =
        document.getElementById("recommendationList");

    recommendationList.replaceChildren();

    results.recommendations.forEach(item => {
        const li = document.createElement("li");

        li.textContent =
            `${item.department}: ${item.recommendation}`;

        recommendationList.appendChild(li);
    });
}

function processData(records) {
    try {
        const results = ScoringEngine.analyse(records);
        showResults(results);
    } catch (error) {
        alert("Could not process data: " + error.message);
    }
}

// Load sample data
document.getElementById("sampleBtn").addEventListener(
    "click",
    () => processData(sampleData)
);

// Upload CSV file
document.getElementById("csvFile").addEventListener(
    "change",
    event => {
        const file = event.target.files[0];

        if (!file) return;

        if (!file.name.toLowerCase().endsWith(".csv")) {
            alert("Please upload a CSV file.");
            event.target.value = "";
            return;
        }

        const reader = new FileReader();

        reader.onload = () => {
            try {
                const records = parseCSV(reader.result);
                processData(records);
            } catch (error) {
                alert("CSV error: " + error.message);
            }
        };

        reader.onerror = () => {
            alert("The file could not be read.");
        };

        reader.readAsText(file);
    }
);
