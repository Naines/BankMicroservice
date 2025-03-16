package com.nainesh.accounts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//validation made here, all validation requirements
@Data
public class CustomerDto {

    @NotEmpty(message = "Name can't be null or empty")
    @Size(min=5, max=30, message="Name has to be between 5 and 30")
    private String name;

    @NotEmpty(message = "Email can't be null or empty")

    @Email(message="Email should be valid value")
    private String email;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    private AccountsDto accountsDto;
}
