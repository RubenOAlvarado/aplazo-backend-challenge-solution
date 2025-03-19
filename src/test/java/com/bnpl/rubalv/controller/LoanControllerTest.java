package com.bnpl.rubalv.controller;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.dto.request.LoanRequestDto;
import com.bnpl.rubalv.enums.LoanStatus;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class LoanControllerTest {
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

    private String authToken;
    private UUID customerId;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        CreateCustomerRequestDto customerRequest = new CreateCustomerRequestDto();
        customerRequest.setFirstName("Maria");
        customerRequest.setLastName("Gomez");
        customerRequest.setSecondLastName("Lopez");
        customerRequest.setDateOfBirth(LocalDate.now().minusYears(25));

        Response response = given()
                .contentType(ContentType.JSON)
                .body(customerRequest)
                .when()
                .post("/v1/customers");
        String location = response.getHeader("Location");
        this.customerId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void shouldCreateLoan() {
        LoanRequestDto loanRequest = new LoanRequestDto();
        loanRequest.setCustomerId(customerId);
        loanRequest.setAmount(BigDecimal.valueOf(1500.75));

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(loanRequest)
                .when()
                .post("/v1/loans")
                .then()
                .statusCode(201)
                .header("Location", containsString("/v1/loans/"))
                .body("id", notNullValue())
                .body("status", equalTo(LoanStatus.ACTIVE.toString()))
                .body("customerId", equalTo(customerId.toString()));
    }

    @Test
    void shouldReturn400WhenCreatingInvalidLoan() {
        LoanRequestDto invalidLoan = new LoanRequestDto();
        invalidLoan.setCustomerId(customerId);
        invalidLoan.setAmount(BigDecimal.valueOf(-100.0));

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(invalidLoan)
                .when()
                .post("/v1/loans")
                .then()
                .statusCode(400)
                .body("code", equalTo("APZ000002"))
                .body("error", equalTo("INVALID_CUSTOMER_REQUEST"));
    }

    @Test
    void shouldGetLoanById() {
        LoanRequestDto loanRequest = new LoanRequestDto();
        loanRequest.setCustomerId(customerId);
        loanRequest.setAmount(BigDecimal.valueOf(2000.0));

        String location = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(loanRequest)
                .when()
                .post("/v1/loans")
                .then()
                .extract()
                .header("Location");

        String loanId = location.substring(location.lastIndexOf('/') + 1);

        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/v1/loans/{loanId}", loanId)
                .then()
                .statusCode(200)
                .body("id", equalTo(loanId))
                .body("status", notNullValue());
    }

    @Test
    void shouldReturn404WhenLoanNotFound() {
        UUID nonExistentLoanId = UUID.randomUUID();

        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/v1/loans/{loanId}", nonExistentLoanId)
                .then()
                .statusCode(404)
                .body("code", equalTo("APZ000008"))
                .body("error", equalTo("LOAN_NOT_FOUND"));
    }

    @Test
    void shouldReturn400ForInsufficientCredit() {
        LoanRequestDto loanRequest = new LoanRequestDto();
        loanRequest.setCustomerId(customerId);
        loanRequest.setAmount(BigDecimal.valueOf(999_999.99));

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(loanRequest)
                .when()
                .post("/v1/loans")
                .then()
                .statusCode(400)
                .body("code", equalTo("APZ000009"))
                .body("error", equalTo("INSUFFICIENT_CREDIT"));
    }

    @Test
    void shouldReturn400ForInvalidCustomerId() {
        LoanRequestDto invalidLoan = new LoanRequestDto();
        invalidLoan.setCustomerId(UUID.randomUUID());
        invalidLoan.setAmount(BigDecimal.valueOf(500.0));

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(invalidLoan)
                .when()
                .post("/v1/loans")
                .then()
                .statusCode(404)
                .body("error", equalTo("CUSTOMER_NOT_FOUND"));
    }
}
