package com.bnpl.rubalv.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequestDto {
    @Schema(description = "Customer's first name")
    @NotBlank
    private String firstName;

    @Schema(description = "Customer's last name")
    @NotBlank
    private String lastName;

    @Schema(description = "Customer's second last name")
    @NotBlank
    private String secondLastName;

    @Schema(
            description = "Customer's date of birth (YYYY-MM-DD)",
            type = "string",
            format = "date",
            pattern = "^\\d{4}-\\d{2}-\\d{2}$"
    )
    @NotNull
    @Past
    private LocalDate dateOfBirth;
}
