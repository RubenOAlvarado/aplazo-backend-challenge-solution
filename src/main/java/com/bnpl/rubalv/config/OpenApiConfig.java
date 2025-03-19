package com.bnpl.rubalv.config;

import com.bnpl.rubalv.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@SecurityScheme(
     name = "aplazoAuth",
     type = SecuritySchemeType.HTTP,
     scheme = "bearer",
     bearerFormat = "JWT"
)
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addResponses("InternalServerErrorResponse", createInternalErrorResponse())
                        .addResponses("InvalidRequest", createBadRequestResponse())
                        .addResponses("UnauthorizedRequest", createUnauthorizedResponse())
                );
    }

    private ApiResponse createInternalErrorResponse() {
        return new ApiResponse()
                .description("Internal Server Error")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<ErrorResponse>().$ref("#/components/schemas/ErrorResponse"))
                                        .examples(Map.of("default",
                                                        new Example().value(new ErrorResponse(
                                                                "APZ000001",
                                                                "INTERNAL_SERVER_ERROR",
                                                                "Internal server error",
                                                                System.currentTimeMillis() / 1000,
                                                                "/v1/current-path"
                                                        ))
                                                )
                                        )
                        )
                );
    }

    private ApiResponse createBadRequestResponse() {
        return new ApiResponse()
                .description("Invalid Request")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<ErrorResponse>().$ref("#/components/schemas/ErrorResponse"))
                                        .examples(Map.of("default",
                                                        new Example().value(new ErrorResponse(
                                                                "APZ000002",
                                                                "INVALID_REQUEST",
                                                                "Validation error in request parameters",
                                                                System.currentTimeMillis() / 1000,
                                                                "/v1/current-path"
                                                        ))
                                                )
                                        )
                        )
                );
    }

    private ApiResponse createUnauthorizedResponse() {
        return new ApiResponse()
                .description("Unauthorized")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new Schema<ErrorResponse>().$ref("#/components/schemas/ErrorResponse"))
                                        .examples(Map.of("default",
                                                        new Example().value(new ErrorResponse(
                                                                "APZ000004",
                                                                "UNAUTHORIZED",
                                                                "Authentication credentials are missing or invalid",
                                                                System.currentTimeMillis() / 1000,
                                                                "/v1/current-path"
                                                        ))
                                                )
                                        )
                        )
                );
    }
}
