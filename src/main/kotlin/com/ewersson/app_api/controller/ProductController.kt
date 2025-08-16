package com.ewersson.app_api.controller

import com.ewersson.app_api.model.product.ProductDTO
import com.ewersson.app_api.model.user.User
import com.ewersson.app_api.service.ProductService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController // Defines this class as a REST API controller (JSON responses)
@RequestMapping("/products") // Base path: /products
class ProductController(
    @Autowired
    private val productService: ProductService // Service layer for product operations
) {

    @PostMapping // POST /products
    fun createProduct(
        @RequestBody @Valid productDTO: ProductDTO,              // Product data from client
        @AuthenticationPrincipal authenticatedUser: User         // Injects the logged-in user
    ): ResponseEntity<ProductDTO> {
        val createdProduct = productService.createProduct(productDTO, authenticatedUser)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct)
        // Returns 201 Created + product info
    }

    @GetMapping("/all") // GET /products/all
    fun getAllProducts(
        @AuthenticationPrincipal authenticatedUser: User
    ): ResponseEntity<List<ProductDTO>> {
        return try {
            val products = productService.findAllProducts()
            ResponseEntity.ok(products) // 200 OK with product list
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @DeleteMapping("/{id}") // DELETE /products/{id}
    fun deleteProduct(
        @PathVariable id: String,                               // Product ID from URL
        @AuthenticationPrincipal authenticatedUser: User
    ): ResponseEntity<Void> {
        return if (productService.deleteProduct(id)) {
            ResponseEntity.status(HttpStatus.NO_CONTENT).build() // 204 if deleted
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()  // 404 if not found
        }
    }
}
