package com.ewersson.app_api

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppApiRestAssured {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            // Set the base URI and port for all Rest-Assured requests
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 8082
        }
    }

    @Test
    fun `register user`() {
        // Create the request body for registration
        val requestBody = mapOf(
            "login" to "Ewersson2",
            "password" to "ewersson",
            "role" to "ADMIN"
        )

        // GIVEN: the request setup (headers, body, etc.)
        Given {
            contentType(ContentType.JSON) // Set Content-Type to JSON
            body(requestBody)              // Attach the request body
        } When {
            post("/auth/register")         // WHEN: send POST request to /auth/register
        } Then {
            statusCode(200)                // THEN: expect HTTP 200 OK
        }
    }

    @Test
    fun `login user and get JWT`() {
        val requestBody = mapOf(
            "login" to "Ewersson2",
            "password" to "ewersson"
        )

        // Send login request and validate response
        Given {
            contentType(ContentType.JSON)
            body(requestBody)
        } When {
            post("/auth/login")             // POST request to /auth/login
        } Then {
            statusCode(200)                 // Expect HTTP 200 OK
            body("token", notNullValue())   // Expect the JSON response to contain a non-null token
        }
    }

    @Test
    fun `create product with JWT`() {
        // First, login to get the JWT token
        val token = Given {
            contentType(ContentType.JSON)
            body(mapOf("login" to "Ewersson2", "password" to "ewersson"))
        } When {
            post("/auth/login")            // Send login request
        } Extract {
            path<String>("token")          // Extract the "token" field from JSON response
        }

        // Prepare the product body
        val productBody = mapOf(
            "product_name" to "Laptop",
            "image" to "https://example.com/laptop.png",
            "price" to 2500.0,
            "stock" to 10
        )

        // Send POST request to create product with Authorization header
        Given {
            contentType(ContentType.JSON)
            header("Authorization", "Bearer $token") // Attach JWT token in header
            body(productBody)                         // Attach product JSON
        } When {
            post("/products")                         // POST request to /products
        } Then {
            statusCode(201)                           // Expect HTTP 201 Created
            body("product_name", equalTo("Laptop"))   // Verify the product name in response
        }
    }
}
