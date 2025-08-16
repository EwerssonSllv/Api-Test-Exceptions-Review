package com.ewersson.app_api.model.user

enum class UserRole(private val role: String) {
    ADMIN("admin"), // Full privileges
    USER("user")    // Restricted privileges
}