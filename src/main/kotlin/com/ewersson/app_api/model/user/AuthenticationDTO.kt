package com.ewersson.app_api.model.user

@JvmRecord // Immutable data holder (lightweight)
data class AuthenticationDTO(
    val login: String,
    val password: String
)