package com.ayman.transactionservice.model.dto.requset;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateApprovalRequest {
    @Positive
    @NotNull
    private Long transectionId;
    @NotNull
    private Boolean approval;
    private String reasonOfFailure;
}
