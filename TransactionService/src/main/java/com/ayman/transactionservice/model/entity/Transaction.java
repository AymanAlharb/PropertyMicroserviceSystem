package com.ayman.transactionservice.model.entity;

import com.ayman.transactionservice.model.enums.TransectionStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(24) not null")
    private TransectionStatusEnum status;

    @Column(columnDefinition = "varchar(256)")
    private String reasonOfFailure;

    @Column(columnDefinition = "date not null")
    private LocalDateTime date;


    private Long propertyId;


    private Long buyerId;

    private Long sellerId;


    private Long brokerId;
}
