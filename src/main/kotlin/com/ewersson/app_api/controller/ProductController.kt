package com.ewersson.app_api.controller

import com.ewersson.app_api.model.product.ProductDTO
import com.ewersson.app_api.model.user.User
import com.ewersson.app_api.service.ProductService
import jakarta.validation.Valid
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    fun createProduct(
        @RequestBody @Valid productDTO: ProductDTO,
        @AuthenticationPrincipal authenticatedUser: User
    ): ResponseEntity<EntityModel<ProductDTO>> {
        val createdProduct = productService.createProduct(productDTO, authenticatedUser)

        val resource = EntityModel.of(createdProduct)

        // Link self
        resource.add(
            linkTo(methodOn(ProductController::class.java)
                .createProduct(productDTO, authenticatedUser))
                .withSelfRel()
        )

        // Link to all products
        resource.add(
            linkTo(methodOn(ProductController::class.java)
                .getAllProducts(authenticatedUser))
                .withRel("all-products")
        )

        // Link to delete, only if id is not null
        createdProduct.id?.let { id ->
            resource.add(
                linkTo(methodOn(ProductController::class.java)
                    .deleteProduct(id, authenticatedUser))
                    .withRel("delete")
            )
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(resource)
    }


    @GetMapping("/all")
    fun getAllProducts(
        @AuthenticationPrincipal authenticatedUser: User
    ): ResponseEntity<CollectionModel<EntityModel<ProductDTO>>> {
        return try {
            val products = productService.findAllProducts()

            val productResources = products.map { product ->
                val resource = EntityModel.of(product)

                // Link to create product
                resource.add(
                    linkTo(methodOn(ProductController::class.java)
                        .createProduct(product, authenticatedUser))
                        .withRel("create")
                )

                // Link to delete product only if id is not null
                product.id?.let { id ->
                    resource.add(
                        linkTo(methodOn(ProductController::class.java)
                            .deleteProduct(id, authenticatedUser))
                            .withRel("delete")
                    )
                }

                // Link to list all
                resource.add(
                    linkTo(methodOn(ProductController::class.java)
                        .getAllProducts(authenticatedUser))
                        .withRel("all-products")
                )

                resource
            }

            val collection = CollectionModel.of(productResources)
            collection.add(
                linkTo(methodOn(ProductController::class.java)
                    .getAllProducts(authenticatedUser))
                    .withSelfRel()
            )

            ResponseEntity.ok(collection)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }


    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: String,
        @AuthenticationPrincipal authenticatedUser: User
    ): ResponseEntity<EntityModel<Any>> {
        return if (productService.deleteProduct(id)) {
            val response: EntityModel<Any> = EntityModel.of<Any>("Product deleted successfully")
            response.add(
                linkTo(methodOn(ProductController::class.java)
                    .getAllProducts(authenticatedUser))
                    .withRel("all-products")
            )
            ResponseEntity.status(HttpStatus.NO_CONTENT).body(response)
        } else {
            val response: EntityModel<Any> = EntityModel.of<Any>("Product not found")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        }
    }


}
