package com.ayman.paymentservice.model.dto.external;

import com.ayman.paymentservice.model.enums.PropertyStatusEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyWrapper {
    private Long id;

    private String title;

    private String description;

    private PropertyStatusEnum status;

    private double price;

    private Long brokerId;

    private Long ownerId;
}
