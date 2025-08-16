package com.ewersson.app_api.repository

import com.ewersson.app_api.model.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    // Standard CRUD operations for User entity

    fun findByLogin(login: String?): UserDetails?
    // Custom query method: finds a user by login
    // Returns UserDetails for Spring Security authentication
    // Spring Data automatically implements this method based on naming convention
}