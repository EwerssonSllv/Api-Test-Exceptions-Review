package com.ewersson.app_api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.ewersson.app_api.model.user.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service // Marks this class as a service component (business logic)
class TokenService {
    @Value("\${api.security.token.secret}")
    private val secret: String? = null // Secret key (from application properties)

    fun generateToken(user: User): String {
        try {
            val algorithm = Algorithm.HMAC256(secret) // Defines signing algorithm
            val token = JWT.create()
                .withIssuer("app-api-review")      // Identifies the token issuer
                .withSubject(user.getUserLogin())  // Associates token with user login
                .withExpiresAt(genExpirationDate())// Sets token expiration
                .sign(algorithm)                   // Signs token with secret key
            return token
        } catch (exception: JWTCreationException) {
            throw RuntimeException("Error while generating token", exception)
        }
    }

    fun validateToken(token: String?): String {
        try {
            val algorithm = Algorithm.HMAC256(secret)
            return JWT.require(algorithm)
                .withIssuer("app-api-review") // Token must match this issuer
                .build()
                .verify(token)                // Verifies signature and expiration
                .subject                      // Returns user login (subject)
        } catch (exception: JWTVerificationException) {
            return "" // Invalid token → return empty string
        }
    }

    // Generates expiration date (24h from now, using UTC-3 offset)
    private fun genExpirationDate(): Instant {
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"))
    }
}
