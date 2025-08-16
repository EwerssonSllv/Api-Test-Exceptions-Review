package com.ewersson.app_api.config

import jakarta.servlet.Filter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration // Marks this class as a Spring configuration
@EnableWebSecurity // Enables Spring Security
class SecurityConfigurations {

    @Autowired
    lateinit var securityFilter: Filter // Custom filter (e.g., JWT validation)

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { } // Enables CORS support
            .csrf { it.disable() } // Disables CSRF (not needed for stateless APIs)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            // Stateless: no sessions, each request must be authenticated (e.g., JWT)

            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/auth/register").permitAll() // Public endpoint
                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()    // Public endpoint
                    // Only users with role USER can access these
                    .requestMatchers(HttpMethod.POST, "/products").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/products/all").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/products/{productID}").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/products/{productName}").hasRole("USER")
                    .anyRequest().authenticated() // Any other request must be authenticated
            }
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter::class.java)
            // Custom filter runs before Spring's default auth filter
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://127.0.0.1:5500", "http://localhost:5500")
        // Allowed client origins
        configuration.allowedMethods = listOf("POST", "GET", "PUT", "DELETE")
        // Allowed HTTP methods
        configuration.allowedHeaders = listOf("Content-Type", "Authorization")
        // Allowed request headers

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Apply CORS to all routes
        return source
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
        // Provides authentication manager for login/authentication
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
        // Password hashing algorithm (secure, salted)
    }

}
