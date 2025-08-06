package com.ayman.authservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLoginRequest {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
