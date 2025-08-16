package com.ewersson.app_api.service

import com.ewersson.app_api.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    private val repository: UserRepository
) : UserDetailsService {

    // Loads a user by username (login) for authentication
    override fun loadUserByUsername(username: String): UserDetails {
        return repository.findByLogin(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        // Throws exception if the user does not exist
        // Spring Security uses this method during login
    }
}
