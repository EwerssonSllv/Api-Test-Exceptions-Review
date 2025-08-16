package com.ewersson.app_api.model.product

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "products")
data class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true)
    var id: String? = null,

    @Column(name="product_name", nullable = false)
    var product_name: String,

    @Column(name = "image", nullable = false)
    var image: String,

    @Column(name = "price", nullable = false)
    var price: Double,

    @Column(name = "stock", nullable = false)
    var stock: Int
){

    // Equality check based only on ID (important for JPA entity identity)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Product) return false
        return id == other.id
    }

    // Hash code also based on ID
    override fun hashCode(): Int {
        return id.hashCode()
    }

}