package com.ayman.paymentservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(16) not null")
    private String accountNumber;

    @Column(columnDefinition = "double not null")
    private double balance;


    private Long userId;
}
