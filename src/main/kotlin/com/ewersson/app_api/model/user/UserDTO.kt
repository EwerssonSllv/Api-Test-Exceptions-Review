package com.ewersson.app_api.model.user

data class UserDTO(
    val id: String,
    val login: String,
    val role: UserRole
) {
    companion object {
        // Converts User entity → UserDTO
        fun fromEntity(user: User) = UserDTO(
            id = user.id!!,
            login = user.login,
            role = user.role
        )
    }
}