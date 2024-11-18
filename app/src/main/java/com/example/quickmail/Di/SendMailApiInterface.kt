package com.example.quickmail.Di

import com.example.quickmail.model.SendGridEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

const val API_KEY = "your ApiKey  "
interface SendMailApiInterface {
    @Headers("Authorization: Bearer $API_KEY", "Content-Type: application/json")
    @POST("v3/mail/send")
    suspend fun sendEmail(@Body emailRequest: SendGridEmailRequest): Response<Void>
}

