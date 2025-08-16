package com.ewersson.app_api.config

import com.ewersson.app_api.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component // Marks this class as a Spring-managed component
class SecurityFilter(
    private val tokenService: TokenService,     // Service to validate JWT tokens
    private val userRepository: UserRepository // Repository to fetch user info
) : OncePerRequestFilter() {
    // Ensures this filter runs once per request

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = recoverToken(request) // Extracts token from request header
        if (token != null) {
            val login = tokenService.validateToken(token) // Validates token and gets user login
            val user = userRepository.findByLogin(login)  // Loads user from database

            if (user != null) {
                // Creates an authentication object with user details and authorities (roles)
                val authentication = UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.authorities
                )
                // Stores authentication in the security context for this request
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
        // Continues with the next filter in the chain
    }

    // Helper method: extracts "Bearer <token>" from Authorization header
    private fun recoverToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization") ?: return null
        return if (authHeader.startsWith("Bearer ")) {
            authHeader.substring(7) // Removes "Bearer " prefix
        } else null
    }
}
