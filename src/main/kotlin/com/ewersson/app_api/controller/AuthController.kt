package com.ewersson.app_api.controller

import com.ewersson.app_api.config.TokenService
import com.ewersson.app_api.model.user.AuthenticationDTO
import com.ewersson.app_api.model.user.LoginResponseDTO
import com.ewersson.app_api.model.user.RegisterDTO
import com.ewersson.app_api.model.user.User
import com.ewersson.app_api.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController // Marks this as a REST controller (returns JSON responses)
@RequestMapping("auth") // Base path: /auth
class AuthController @Autowired constructor(
    private val authenticationManager: AuthenticationManager, // Handles authentication
    private val repository: UserRepository,                   // Access to user data
    private val tokenService: TokenService,                   // Generates JWT tokens
    private val passwordEncoder: BCryptPasswordEncoder        // Encrypts passwords
) {

    @PostMapping("/login") // POST /auth/login
    fun login(@RequestBody data: @Valid AuthenticationDTO): ResponseEntity<LoginResponseDTO> {
        // Creates authentication token from user input
        val usernamePassword = UsernamePasswordAuthenticationToken(data.login, data.password)
        val auth = authenticationManager.authenticate(usernamePassword)
        // Validates credentials using Spring Security

        val token = tokenService.generateToken(auth.principal as User)
        // Generates JWT for authenticated user

        return ResponseEntity.ok(LoginResponseDTO(token))
        // Returns token to client
    }

    @PostMapping("/register") // POST /auth/register
    fun register(@RequestBody data: @Valid RegisterDTO): ResponseEntity<Any> {
        // If login already exists → reject
        if (repository.findByLogin(data.login) != null)
            return ResponseEntity.badRequest().build<Any>()

        // Encrypt password before saving
        val encryptedPassword = passwordEncoder.encode(data.password)
        val newUser = User(data.role, data.login, encryptedPassword)

        repository.save(newUser) // Store new user in database

        return ResponseEntity.ok().build<Any>() // Success response
    }
}
