package com.safespend.ai.api

import com.safespend.ai.models.AgentStateResponse
import com.safespend.ai.models.FeedbackRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/state")
    suspend fun getCurrentState(): Response<AgentStateResponse>

    @POST("api/inject-delay")
    suspend fun injectIncomeDelay(): Response<AgentStateResponse>

    @POST("api/add-expense")
    suspend fun addExpenseShock(): Response<AgentStateResponse>

    @POST("api/user-feedback")
    suspend fun sendUserFeedback(@Body feedback: FeedbackRequest): Response<AgentStateResponse>

    @POST("api/reset-demo")
    suspend fun resetDemo(): Response<AgentStateResponse>
}