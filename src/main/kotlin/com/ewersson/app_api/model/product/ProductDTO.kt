package com.ewersson.app_api.model.product

@JvmRecord
data class ProductDTO(
    val id: String? = null,
    val product_name: String,
    val image: String,
    val price: Double,
    val stock: Int
){
    companion object {
        // Converts a Product entity into a ProductDTO
        fun fromEntity(product: Product): ProductDTO {
            return ProductDTO(
                id = product.id,
                product_name = product.product_name,
                image = product.image,
                price = product.price,
                stock = product.stock
            )
        }
    }
}