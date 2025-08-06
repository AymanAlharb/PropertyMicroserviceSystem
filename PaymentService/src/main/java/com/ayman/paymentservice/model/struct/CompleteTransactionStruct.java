package com.ayman.paymentservice.model.struct;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteTransactionStruct {
    private Long transactionId;
    private Long oldOwnerId;
    private Long newOwnerId;
    private Long propertyId;
}
