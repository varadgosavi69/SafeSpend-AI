
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from models import AgentStateResponse, ActionRecommendation, FeedbackRequest
from formula_engine import calculate_state

app = FastAPI(title="SafeSpend.AI Backend")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

DEMO_DEFAULTS = {
    "current_balance": 14250.0,
    "safety_buffer": 2000.0,
    "weighted_income": 6000.0,
    "committed_expenses": 6812.0,
}

state = dict(DEMO_DEFAULTS)
action_approved = False


def build_response() -> AgentStateResponse:
    math_result = calculate_state(
        current_balance=state["current_balance"],
        weighted_income=state["weighted_income"],
        committed_expenses=state["committed_expenses"],
        safety_buffer=state["safety_buffer"],
    )

    explanation = (
        "Your committed expenses are close to exceeding your safe liquidity buffer. "
        "Consider pausing non-essential subscriptions."
        if math_result.shortfall_risk
        else "Your spending is within safe limits based on current income and committed expenses."
    )

    return AgentStateResponse(
        current_balance=state["current_balance"],
        safety_buffer=state["safety_buffer"],
        math_result=math_result,
        explanation=explanation,
        recommended_action=ActionRecommendation(
            action_id="pause_ott",
            title="Pause OTT Subscriptions",
            impact="Saves approximately ₹500 over 14 days",
            type="SUBSCRIPTION_PAUSE",
        ),
    )


@app.get("/api/state", response_model=AgentStateResponse)
def get_state():
    return build_response()


@app.post("/api/inject-delay", response_model=AgentStateResponse)
def inject_income_delay():
    state["weighted_income"] *= 0.2
    return build_response()


@app.post("/api/add-expense", response_model=AgentStateResponse)
def add_expense_shock():
    state["committed_expenses"] += 1500.0
    return build_response()


@app.post("/api/user-feedback", response_model=AgentStateResponse)
def user_feedback(feedback: FeedbackRequest):
    global action_approved
    if feedback.action == "APPROVE" and not action_approved:
        state["committed_expenses"] -= 500.0
        action_approved = True
    return build_response()


@app.post("/api/reset-demo", response_model=AgentStateResponse)
def reset_demo():
    global action_approved
    state.update(DEMO_DEFAULTS)
    action_approved = False
    return build_response()
