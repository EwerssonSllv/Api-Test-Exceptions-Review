package com.ewersson.app_api.controller

import com.ewersson.app_api.config.TokenService
import com.ewersson.app_api.model.user.AuthenticationDTO
import com.ewersson.app_api.model.user.LoginResponseDTO
import com.ewersson.app_api.model.user.RegisterDTO
import com.ewersson.app_api.model.user.User
import com.ewersson.app_api.model.user.UserRole
import com.ewersson.app_api.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
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

    @PostMapping("/login")
    fun login(@RequestBody data: @Valid AuthenticationDTO): ResponseEntity<EntityModel<LoginResponseDTO>> {
        val usernamePassword = UsernamePasswordAuthenticationToken(data.login, data.password)
        val auth = authenticationManager.authenticate(usernamePassword)
        val token = tokenService.generateToken(auth.principal as User)

        val response = EntityModel.of(LoginResponseDTO(token))
        response.add(linkTo(methodOn(AuthController::class.java).login(data)).withSelfRel())
        response.add(linkTo(methodOn(AuthController::class.java).register(RegisterDTO("", "", ""))).withRel("register"))

        return ResponseEntity.ok(response)
    }

    @PostMapping("/register")
    fun register(@RequestBody data: @Valid RegisterDTO): ResponseEntity<EntityModel<Any>> {
        if (repository.findByLogin(data.login) != null)
            return ResponseEntity.badRequest().build()

        val encryptedPassword = passwordEncoder.encode(data.password)

        val roleEnum = try {
            UserRole.valueOf(data.role.uppercase())
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest()
                .body(EntityModel.of("Invalid role: ${data.role}"))
        }

        val newUser = User(roleEnum, data.login, encryptedPassword)
        repository.save(newUser)

        val response: EntityModel<Any> = EntityModel.of("User registered successfully")
        response.add(linkTo(methodOn(AuthController::class.java).register(data)).withSelfRel())
        response.add(linkTo(methodOn(AuthController::class.java).login(AuthenticationDTO(data.login, data.password))).withRel("login"))

        return ResponseEntity.ok(response)
    }


}
