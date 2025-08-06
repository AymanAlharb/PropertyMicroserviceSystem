package com.ayman.authservice.model.dto.request.keycloak;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAssignRoleRequest {
    private String id;
    private String name;
}
