package com.ewersson.app_api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedHeaders("*")
            .allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS")
    }

    /**
     * Configures a StrictHttpFirewall to allow URL-encoded slashes ("%2F") in request URLs.
     * By default, Spring Security blocks them for security reasons to prevent path traversal attacks.
     * Only enable this if your application specifically requires encoded slashes in paths,
     * and ensure proper validation and sanitization of input.
     **/
    @Bean
    fun allowUrlEncodedSlashHttpFirewall(): HttpFirewall {
        val firewall = StrictHttpFirewall()
        firewall.setAllowUrlEncodedSlash(true)
        return firewall
    }
}