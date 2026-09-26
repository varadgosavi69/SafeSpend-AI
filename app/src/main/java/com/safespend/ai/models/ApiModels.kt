package com.safespend.ai.models

import com.google.gson.annotations.SerializedName

data class DayPoint(
    @SerializedName("day") val day: String,
    @SerializedName("projected_balance") val projectedBalance: Double,
    @SerializedName("safety_buffer") val safetyBuffer: Double
)

data class MathResult(
    @SerializedName("current_balance") val currentBalance: Double,
    @SerializedName("total_weighted_income") val totalWeightedIncome: Double,
    @SerializedName("total_committed_expenses") val totalCommittedExpenses: Double,
    @SerializedName("usable_liquidity_pool") val usableLiquidityPool: Double,
    @SerializedName("daily_safe_to_spend") val dailySafeToSpend: Double,
    @SerializedName("shortfall_risk") val shortfallRisk: Boolean,
    @SerializedName("daily_trajectory") val dailyTrajectory: List<DayPoint>
)

data class ActionRecommendation(
    @SerializedName("action_id") val actionId: String,
    @SerializedName("title") val title: String,
    @SerializedName("impact") val impact: String,
    @SerializedName("type") val type: String
)

data class AgentStateResponse(
    @SerializedName("current_balance") val currentBalance: Double,
    @SerializedName("safety_buffer") val safetyBuffer: Double,
    @SerializedName("math_result") val mathResult: MathResult,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("recommended_action") val recommendedAction: ActionRecommendation
)

data class FeedbackRequest(
    @SerializedName("action") val action: String
)
