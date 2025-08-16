package com.ewersson.app_api.service

import com.ewersson.app_api.model.product.Product
import com.ewersson.app_api.model.product.ProductDTO
import com.ewersson.app_api.model.user.User
import com.ewersson.app_api.repository.ProductRepository
import com.ewersson.app_api.repository.UserRepository
import com.ewersson.app_api.service.exceptions.ObjectNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service // Marks this class as a service component (business logic layer)
class ProductService
@Autowired
constructor(
    private val productRepository: ProductRepository, // Access to Product database
    private val userRepository: UserRepository        // Access to User database
) {

    // Creates a new product
    fun createProduct(productDTO: ProductDTO, authenticatedUser: User): ProductDTO {
        // Verify that the authenticated user exists in the database
        val user = userRepository.findById(authenticatedUser.id!!)
            .orElseThrow { IllegalArgumentException("User not found!") }

        // Convert DTO to entity
        val product = Product(
            product_name = productDTO.product_name,
            image = productDTO.image,
            price = productDTO.price,
            stock = productDTO.stock,
        )

        // Save product to database
        val savedProduct = productRepository.save(product)

        // Convert saved entity back to DTO and return
        return ProductDTO.fromEntity(savedProduct)
    }

    // Returns all products as a list of DTOs
    fun findAllProducts(): List<ProductDTO> {
        val products = productRepository.findAll()
        return products.map { ProductDTO.fromEntity(it) }
    }

    // Deletes a product by ID; returns true if deleted, false if not found
    fun deleteProduct(id: String): Boolean {
        return if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    // Updates the stock quantity of a product
    fun updateStock(productId: String, quantity: Int): ProductDTO {
        val product = productRepository.findById(productId)
            .orElseThrow { ObjectNotFoundException("Product not found!") }

        product.stock = quantity
        val updatedProduct = productRepository.save(product)

        return ProductDTO.fromEntity(updatedProduct)
    }
}
