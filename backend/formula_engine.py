from models import MathResult, DayPoint


def calculate_state(
    current_balance: float,
    weighted_income: float,
    committed_expenses: float,
    safety_buffer: float,
) -> MathResult:
    usable_liquidity_pool = (
        current_balance + weighted_income - committed_expenses - safety_buffer
    )

    daily_safe_to_spend = max(0.0, usable_liquidity_pool) / 14.0

    shortfall_risk = usable_liquidity_pool <= 0

    per_day_net_income = (weighted_income - committed_expenses) / 14.0

    daily_trajectory = []
    for day in range(1, 15):
        projected_balance = (
            current_balance + (per_day_net_income * day) - (daily_safe_to_spend * day)
        )
        daily_trajectory.append(
            DayPoint(
                day=f"Day {day}",
                projected_balance=round(projected_balance, 2),
                safety_buffer=safety_buffer,
            )
        )

    return MathResult(
        current_balance=current_balance,
        total_weighted_income=weighted_income,
        total_committed_expenses=committed_expenses,
        usable_liquidity_pool=round(usable_liquidity_pool, 2),
        daily_safe_to_spend=round(daily_safe_to_spend, 2),
        shortfall_risk=shortfall_risk,
        daily_trajectory=daily_trajectory,
    )