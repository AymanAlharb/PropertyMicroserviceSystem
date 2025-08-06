package com.ayman.authservice.model.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserResponse {
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
