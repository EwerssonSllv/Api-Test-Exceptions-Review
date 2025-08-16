package com.ewersson.app_api.model.user

@JvmRecord
data class LoginResponseDTO(
    val token: String // JWT token returned after successful login
)
