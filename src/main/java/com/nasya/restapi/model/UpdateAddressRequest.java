package com.nasya.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class UpdateAddressRequest {

    @NotBlank
    @JsonIgnore
    private String addressId;

    @JsonIgnore
    @NotBlank
    private String contactId;

    @NotBlank
    private String country;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String province;

    @Size(max = 100)
    private String street;

    @Size(max = 10)
    private String postalCode;

}
