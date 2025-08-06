package com.ayman.paymentservice.model.dto.external;

import com.ayman.paymentservice.model.enums.TransectionStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class TransactionWrapper {
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
