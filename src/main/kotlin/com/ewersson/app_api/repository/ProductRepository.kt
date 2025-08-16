package com.ewersson.app_api.repository

import com.ewersson.app_api.model.product.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, String>
// Provides CRUD operations for Product entity
// - Inherits standard methods like save, findById, findAll, deleteById
// - ID type is String (UUID)