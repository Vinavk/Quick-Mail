package com.example.quickmail.model


data class SendGridEmailRequest(
    val personalizations: List<Personalization>,
    val from: Email,
    val subject: String,
    val content: List<Content>
)

data class Personalization(
    val to: List<Email>
)

data class Email(
    val email: String
)

data class Content(
    val type: String,
    val value: String
)

data class MailProperties(var SenderName : String,var Subject : String, var Content : String)
