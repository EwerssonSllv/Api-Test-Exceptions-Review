package com.ewersson.app_api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductApiSpringTest {

	// Inject TestRestTemplate to simulate HTTP requests to the application
	@Autowired
	lateinit var restTemplate: TestRestTemplate

	@Test
	fun `register user`() {
		// Create a map representing the user registration request body
		val requestBody = mapOf(
			"login" to "testuser",
			"password" to "123456",
			"role" to "ADMIN"
		)

		// Send a POST request to /auth/register with the request body
		val response = restTemplate.postForEntity("/auth/register", requestBody, Void::class.java)

		// Assert that the response HTTP status code is 200 OK
		assert(response.statusCode == HttpStatus.OK)
	}

	@Test
	fun `login user`() {
		// Create a map representing the login request body
		val requestBody = mapOf(
			"login" to "testuser",
			"password" to "123456"
		)

		// Send a POST request to /auth/login with the request body
		val response = restTemplate.postForEntity("/auth/login", requestBody, Map::class.java)

		// Assert that the response status is 200 OK
		assert(response.statusCode == HttpStatus.OK)

		// Assert that the response body contains a token
		assert(response.body?.get("token") != null)
	}

	@Test
	fun `create product`() {
		// First, login to get JWT token
		val loginBody = mapOf("login" to "testuser", "password" to "123456")
		val loginResponse = restTemplate.postForEntity("/auth/login", loginBody, Map::class.java)
		val token = loginResponse.body?.get("token") as String

		// Create a map representing the product to create
		val productBody = mapOf(
			"product_name" to "Laptop",
			"image" to "https://example.com/laptop.png",
			"price" to 2500.0,
			"stock" to 10
		)

		// Prepare HTTP headers including Authorization with JWT token
		val headers = HttpHeaders()
		headers.set("Authorization", "Bearer $token")
		headers.contentType = MediaType.APPLICATION_JSON

		// Combine headers and body into an HttpEntity
		val entity = HttpEntity(productBody, headers)

		// Send a POST request to /products with the HttpEntity
		val response = restTemplate.exchange("/products", HttpMethod.POST, entity, Map::class.java)

		// Assert that the response status is 201 CREATED
		assert(response.statusCode == HttpStatus.CREATED)

		// Assert that the response body contains the correct product_name
		assert(response.body?.get("product_name") == "Laptop")
	}
}

