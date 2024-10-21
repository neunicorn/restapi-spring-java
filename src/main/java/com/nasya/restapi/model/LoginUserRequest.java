package com.nasya.restapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserRequest {

    @Size(max = 100)
    @NotBlank
    private String username;

    @Size(min = 6, max = 100)
    @NotBlank
    private String password;

}
