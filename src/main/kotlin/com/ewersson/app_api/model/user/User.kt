package com.ewersson.app_api.model.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true)
    val id: String? = null,

    @Column(name = "login", nullable = false)
    var login: String,

    @Column(name = "password", nullable = false)
    private val password: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val role: UserRole

): UserDetails {

    // Equality & hashcode based on ID
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()

    // Secondary constructor for easier instantiation
    constructor(role: UserRole, login: String, password: String)
            : this(null, login, password, role)

    // Defines user authorities (roles)
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return if (role == UserRole.ADMIN) {
            listOf(
                SimpleGrantedAuthority("ROLE_ADMIN"),
                SimpleGrantedAuthority("ROLE_USER") // Admins also get user privileges
            )
        } else {
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        }
    }

    // Utility method to get username
    fun getUserLogin(): String? = login

    // UserDetails contract implementation
    override fun getPassword(): String = password
    override fun getUsername(): String? = login
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}