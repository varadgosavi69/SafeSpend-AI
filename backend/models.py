
from pydantic import BaseModel
from typing import List


class DayPoint(BaseModel):
    day: str
    projected_balance: float
    safety_buffer: float


class MathResult(BaseModel):
    current_balance: float
    total_weighted_income: float
    total_committed_expenses: float
    usable_liquidity_pool: float
    daily_safe_to_spend: float
    shortfall_risk: bool
    daily_trajectory: List[DayPoint]


class ActionRecommendation(BaseModel):
    action_id: str
    title: str
    impact: str
    type: str


class AgentStateResponse(BaseModel):
    current_balance: float
    safety_buffer: float
    math_result: MathResult
    explanation: str
    recommended_action: ActionRecommendation


class FeedbackRequest(BaseModel):
    action: str  # "APPROVE" or "REJECT"
