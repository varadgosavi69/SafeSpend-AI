package com.safespend.ai.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safespend.ai.api.RetrofitClient
import com.safespend.ai.models.AgentStateResponse
import com.safespend.ai.models.FeedbackRequest
import com.safespend.ai.ui.components.AnimatedRupeeText
import com.safespend.ai.ui.components.formatRupeesShared
import com.safespend.ai.ui.components.pressScale
import com.safespend.ai.ui.theme.*
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardState {
    var agentState by mutableStateOf<AgentStateResponse?>(null)
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    val currentBalance: Double get() = agentState?.currentBalance ?: 0.0
    val safetyBuffer: Double get() = agentState?.safetyBuffer ?: 0.0
    val weightedIncome: Double get() = agentState?.mathResult?.totalWeightedIncome ?: 0.0
    val committedExpenses: Double get() = agentState?.mathResult?.totalCommittedExpenses ?: 0.0
    val usableLiquidityPool: Double get() = agentState?.mathResult?.usableLiquidityPool ?: 0.0
    val dailySafeToSpend: Double get() = agentState?.mathResult?.dailySafeToSpend ?: 0.0
    val shortfallRisk: Boolean get() = agentState?.mathResult?.shortfallRisk ?: false
    val explanation: String get() = agentState?.explanation ?: ""
    val actionTitle: String get() = agentState?.recommendedAction?.title ?: "Pause OTT Subscriptions"

    val dailyProjection: List<Double>
        get() = agentState?.mathResult?.dailyTrajectory?.map { it.projectedBalance } ?: emptyList()

    val trajectory: List<Float>
        get() = dailyProjection.map { it.toFloat() }.ifEmpty { listOf(0f) }

    suspend fun loadState() {
        isLoading = true
        errorMessage = null
        try {
            val response = RetrofitClient.apiService.getCurrentState()
            if (response.isSuccessful && response.body() != null) {
                agentState = response.body()
            } else {
                errorMessage = "Failed to load data (code ${response.code()})"
            }
        } catch (e: Exception) {
            errorMessage = "Unable to connect to backend. Make sure it's running."
        } finally {
            isLoading = false
        }
    }

    suspend fun injectIncomeDelay(): Boolean = performAction { RetrofitClient.apiService.injectIncomeDelay() }

    suspend fun addExpenseShock(): Boolean = performAction { RetrofitClient.apiService.addExpenseShock() }

    suspend fun approveAction(): Boolean =
        performAction { RetrofitClient.apiService.sendUserFeedback(FeedbackRequest("APPROVE")) }

    suspend fun rejectAction(): Boolean =
        performAction { RetrofitClient.apiService.sendUserFeedback(FeedbackRequest("REJECT")) }

    suspend fun reset(): Boolean = performAction { RetrofitClient.apiService.resetDemo() }

    private suspend fun performAction(call: suspend () -> Response<AgentStateResponse>): Boolean {
        isLoading = true
        errorMessage = null
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                agentState = response.body()
                true
            } else {
                errorMessage = "Action failed (code ${response.code()}). Please try again."
                false
            }
        } catch (e: Exception) {
            errorMessage = "Unable to connect to backend. Please try again."
            false
        } finally {
            isLoading = false
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen() {
    val state = remember { DashboardState() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        state.loadState()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { scope.launch { state.loadState() } }
    )

    Scaffold(
        containerColor = DashboardBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            com.safespend.ai.ui.components.FloatingBottomNav(
                items = listOf(
                    com.safespend.ai.ui.components.NavItem(Icons.Filled.Home, "Home"),
                    com.safespend.ai.ui.components.NavItem(Icons.Filled.InsertChart, "Insights"),
                    com.safespend.ai.ui.components.NavItem(Icons.Filled.Person, "Profile")
                ),
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->

        if (state.agentState == null && state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = GradientGreenEnd)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Loading your data...", color = TextSecondaryGray, fontSize = 13.sp)
                }
            }
            return@Scaffold
        }

        if (state.agentState == null && state.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        state.errorMessage ?: "Something went wrong",
                        color = TextSecondaryGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val retryInteraction = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = GradientGreenEnd,
                        modifier = Modifier
                            .pressScale(retryInteraction)
                            .clickable(interactionSource = retryInteraction, indication = null) {
                                scope.launch { state.loadState() }
                            }
                    ) {
                        Text(
                            "Retry",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                        )
                    }
                }
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) },
                label = "tabSwitch"
            ) { tab ->
                when (tab) {
                    1 -> InsightsScreen(state = state)
                    2 -> ProfileScreen()
                    else -> HomeTab(
                        state = state,
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = GradientGreenEnd
            )
        }
    }
}

// ================================================================
// Redesigned HomeTab — paste this in place of the existing HomeTab()
// function inside DashboardScreen.kt. Uses the same DashboardState,
// same theme colors (GradientGreenStart/End, TextDarkGreen,
// TextSecondaryGray, CardSurface, ChipTintGreen, AccentGold,
// StatusWarningRed, StatusSafeGreen) that are already defined in
// your ui/theme package — no new colors needed.
// ================================================================

@Composable
private fun HomeTab(
    state: DashboardState,
    scope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // ---------- Top bar: brand + greeting + avatar ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "safespend.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkGreen
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = TextDarkGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Brush.linearGradient(listOf(GradientGreenStart, GradientGreenEnd)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("B", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Hi there,", fontSize = 13.sp, color = TextSecondaryGray)
        Text(
            "Welcome Back!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextDarkGreen
        )
        Text(
            "Here's your latest safe-to-spend overview",
            fontSize = 12.sp,
            color = TextSecondaryGray
        )

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StatusWarningRed.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(state.errorMessage ?: "", color = StatusWarningRed, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text(
                        "Retry",
                        color = StatusWarningRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { scope.launch { state.loadState() } }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- Big dark gradient balance-style card ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientGreenStart, GradientGreenEnd),
                            start = Offset(0f, 0f),
                            end = Offset(900f, 900f)
                        )
                    )
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Balance", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                        Text(
                            formatRupeesShared(state.currentBalance),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(shape = RoundedCornerShape(50), color = ChipTranslucentWhite) {
                        Text(
                            text = if (state.shortfallRisk) "RISK" else "SAFE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Divider(color = Color.White.copy(alpha = 0.18f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                Text("Daily Safe-to-Spend", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                AnimatedRupeeText(target = state.dailySafeToSpend, suffix = " /day")

                Spacer(modifier = Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { scope.launch { state.loadState() } }
                    ) {
                        Text(
                            "Refresh",
                            color = GradientGreenEnd,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = ChipTranslucentWhite,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { scope.launch { state.addExpenseShock() } }
                    ) {
                        Text(
                            "Simulate",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- Two white stat cards side by side ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Expenses", fontSize = 12.sp, color = TextSecondaryGray)
                        Icon(Icons.Filled.Receipt, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        formatRupeesShared(state.committedExpenses),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkGreen
                    )
                    Text("Committed this month", fontSize = 10.sp, color = TextSecondaryGray)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Buffer", fontSize = 12.sp, color = TextSecondaryGray)
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = GradientGreenEnd, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        formatRupeesShared(state.safetyBuffer),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkGreen
                    )
                    Text("Safety cushion", fontSize = 10.sp, color = TextSecondaryGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("AI Guardian Insight", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).background(ChipTintGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = GradientGreenEnd, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(state.actionTitle, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDarkGreen)
                        Text(
                            text = if (state.shortfallRisk) "Shortfall risk detected" else "Spending within safe limits",
                            fontSize = 12.sp,
                            color = TextSecondaryGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = state.explanation.ifEmpty {
                        if (state.shortfallRisk)
                            "Your committed expenses are close to exceeding your safe liquidity buffer."
                        else
                            "Your spending is within safe limits based on current income and expenses."
                    },
                    fontSize = 12.sp,
                    color = TextSecondaryGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = StatusSafeGreen,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !state.isLoading) {
                                scope.launch {
                                    if (state.approveAction()) {
                                        snackbarHostState.showSnackbar("Approved: ${state.actionTitle}")
                                    }
                                }
                            }
                    ) {
                        Text("Approve", color = Color.White, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, fontSize = 13.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = ChipTintGreen,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !state.isLoading) {
                                scope.launch {
                                    if (state.rejectAction()) {
                                        snackbarHostState.showSnackbar("Action rejected")
                                    }
                                }
                            }
                    ) {
                        Text("Reject", color = TextDarkGreen, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, fontSize = 13.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}