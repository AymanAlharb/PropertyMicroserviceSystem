package com.ayman.authservice.model.entity;

import com.ayman.authservice.model.enums.UserRoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(columnDefinition = "varchar(20) not null unique")
    private String username;

    @Column(columnDefinition = "varchar(256) not null")
    private String password;

    @Column(columnDefinition = "varchar(320) not null unique")
    private String email;

    @Column(columnDefinition = "varchar(16) not null unique")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(8) not null")
    private UserRoleEnum role;

    private Long bankAccountId;

}
