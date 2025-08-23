package com.ewersson.app_api.model.user

@JvmRecord
data class RegisterDTO(
    val role: String,  // Role assigned to new user (ADMIN / USER)
    val login: String,
    val password: String
)
