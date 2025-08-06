package com.ayman.transactionservice.model.dto.external;

import com.ayman.transactionservice.model.enums.PropertyStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PropertyWrapper {
    private Long id;

    private String title;

    private String description;

    private PropertyStatusEnum status;

    private double price;

    private Long brokerId;

    private Long ownerId;
}
