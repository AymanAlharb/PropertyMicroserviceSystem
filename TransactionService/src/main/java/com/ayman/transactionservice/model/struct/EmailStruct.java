package com.ayman.transactionservice.model.struct;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailStruct {
    private Long receiverId;
    private String receiverEmail;
    private String receiverUsername;
    private String body;
    private String subject;
}
