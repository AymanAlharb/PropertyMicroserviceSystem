package com.ayman.propertyservice.model.dto.external;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWrapper {
    @NotNull
    private Long userId;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String role;
}
