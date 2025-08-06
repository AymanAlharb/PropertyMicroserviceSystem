package com.ayman.transactionservice.model.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWrapper {
    private Long userId;

    private String username;

    private String email;

    private String phoneNumber;

    private String role;
}
