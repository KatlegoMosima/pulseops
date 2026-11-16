
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from typing import List, Literal
from collections import defaultdict
import statistics

app = FastAPI(
    title="PulseOps Analytics API",
    description="Operational performance and statistical analytics engine",
    version="1.0.0"
)


class KPIRecord(BaseModel):
    department: str
    kpi: str
    actual: float
    target: float = Field(gt=0)
    weight: float = Field(gt=0)
    direction: Literal["higher", "lower"]


class AnalysisRequest(BaseModel):
    records: List[KPIRecord]


def calculate_score(record):
    if record.direction == "higher":
        score = min(100, record.actual / record.target * 100)
    else:
        score = (
            100 if record.actual <= record.target
            else max(0, record.target / record.actual * 100)
        )

    return round(score, 2)


@app.get("/")
def home():
    return {
        "service": "PulseOps Analytics",
        "status": "running",
        "version": "1.0.0"
    }


@app.get("/health")
def health():
    return {"status": "healthy"}


@app.post("/analyse")
def analyse(request: AnalysisRequest):

    if not request.records:
        raise HTTPException(
            status_code=400,
            detail="At least one KPI record is required."
        )

    scored_records = []

    for record in request.records:
        score = calculate_score(record)

        scored_records.append({
            **record.model_dump(),
            "score": score
        })

    total_weight = sum(
        item["weight"] for item in scored_records
    )

    overall_score = sum(
        item["score"] * item["weight"]
        for item in scored_records
    ) / total_weight

    departments = defaultdict(list)

    for item in scored_records:
        departments[item["department"]].append(item["score"])

    department_results = []

    for department, scores in departments.items():
        department_results.append({
            "department": department,
            "average_score": round(statistics.mean(scores), 2),
            "kpi_count": len(scores)
        })

    priorities = sorted(
        scored_records,
        key=lambda item: item["score"]
    )[:3]

    recommendations = []

    for item in priorities:
        if item["direction"] == "higher":
            action = (
                f"Investigate why {item['kpi']} is below target "
                "and introduce corrective improvements."
            )
        else:
            action = (
                f"Investigate the causes of high {item['kpi']} "
                "and introduce controls to reduce it."
            )

        recommendations.append({
            "department": item["department"],
            "kpi": item["kpi"],
            "score": item["score"],
            "action": action
        })

    return {
        "overall_score": round(overall_score, 2),
        "total_kpis": len(scored_records),
        "kpis_below_80": sum(
            item["score"] < 80 for item in scored_records
        ),
        "department_analysis": department_results,
        "priority_areas": priorities,
        "recommendations": recommendations,
        "records": scored_records
    }
