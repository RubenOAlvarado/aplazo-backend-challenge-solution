package com.bnpl.rubalv.controller;

import com.bnpl.rubalv.TestcontainersConfiguration;
import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class CustomerControllerTest {
    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test-bpnl")
            .withUsername("testUser")
            .withPassword("testing");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void shouldCreateCustomer() {
        CreateCustomerRequestDto customer = new CreateCustomerRequestDto();
        customer.setFirstName("Pepe");
        customer.setLastName("Pruebas");
        customer.setSecondLastName("Integracion");
        customer.setDateOfBirth(LocalDate.now().minusYears(35));

        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when()
                .post("/v1/customers")
                .then()
                .statusCode(201)
                .header("Location", containsString("/v1/customers/"))
                .header("X-Auth-Token", notNullValue())
                .body("id", notNullValue())
                .body("creditLineAmount", notNullValue())
                .body("availableCreditLineAmount", notNullValue());
    }

    @Test
    void shouldReturn400WhenCreatingInvalidCustomer() {
        CreateCustomerRequestDto invalidCustomer = new CreateCustomerRequestDto();
        invalidCustomer.setFirstName("");
        invalidCustomer.setLastName("Pruebas");
        invalidCustomer.setSecondLastName("Integracion");
        invalidCustomer.setDateOfBirth(LocalDate.now().minusYears(10)); // Menor de edad

        given()
                .contentType(ContentType.JSON)
                .body(invalidCustomer)
                .when()
                .post("/v1/customers")
                .then()
                .statusCode(400)
                .body("code", equalTo("APZ000002"))
                .body("error", equalTo("INVALID_CUSTOMER_REQUEST"))
                .body("path", equalTo("/v1/customers"));
    }

    @Test
    void shouldGetCustomerById() {
        CreateCustomerRequestDto customer = new CreateCustomerRequestDto();
        customer.setFirstName("Maria");
        customer.setLastName("Gomez");
        customer.setSecondLastName("Lopez");
        customer.setDateOfBirth(LocalDate.now().minusYears(25));

        String location = given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when()
                .post("/v1/customers")
                .then()
                .extract()
                .header("Location");

        String customerId = location.substring(location.lastIndexOf('/') + 1);

        given()
                .when()
                .get("/v1/customers/{customerId}", customerId)
                .then()
                .statusCode(200)
                .body("id", equalTo(customerId))
                .body("creditLineAmount", notNullValue())
                .body("availableCreditLineAmount", notNullValue())
                .body("createdAt", notNullValue());
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() {
        UUID nonExistentCustomerId = UUID.randomUUID();

        given()
                .when()
                .get("/v1/customers/{customerId}", nonExistentCustomerId)
                .then()
                .statusCode(404)
                .body("code", equalTo("APZ000005"))
                .body("error", equalTo("CUSTOMER_NOT_FOUND"))
                .body("path", equalTo("/v1/customers/" + nonExistentCustomerId));
    }

    @Test
    void shouldReturn401WhenUnauthorized() {
        given()
                .when()
                .get("/v1/customers/{customerId}", UUID.randomUUID())
                .then()
                .log().all()
                .statusCode(401)
                .body("code", equalTo("APZ000007"))
                .body("error", equalTo("UNAUTHORIZED"));
    }

    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer invalid-token")
                .when()
                .get("/v1/customers/{customerId}", UUID.randomUUID())
                .then()
                .statusCode(401)
                .body("code", equalTo("APZ000007"))
                .body("error", equalTo("UNAUTHORIZED"));
    }


    @Test
    void shouldHandleLongNames() {
        CreateCustomerRequestDto customer = new CreateCustomerRequestDto();
        customer.setFirstName("A".repeat(100));
        customer.setLastName("B".repeat(100));
        customer.setSecondLastName("C".repeat(100));
        customer.setDateOfBirth(LocalDate.now().minusYears(30));

        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when()
                .post("/v1/customers")
                .then()
                .statusCode(201)
                .body("creditLineAmount", notNullValue())
                .body("availableCreditLineAmount", notNullValue())
                .body("createdAt", notNullValue());
    }

    @Test
    void shouldReturn400WhenDateOfBirthIsInFuture() {
        CreateCustomerRequestDto customer = new CreateCustomerRequestDto();
        customer.setFirstName("Pepe");
        customer.setLastName("Pruebas");
        customer.setSecondLastName("Integracion");
        customer.setDateOfBirth(LocalDate.now().plusYears(1));

        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when()
                .post("/v1/customers")
                .then()
                .statusCode(400)
                .body("code", equalTo("APZ000002"))
                .body("error", equalTo("INVALID_CUSTOMER_REQUEST"));
    }
}
