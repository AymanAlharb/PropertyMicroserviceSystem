package com.ayman.propertyservice.model.dto.response;

import com.ayman.propertyservice.model.enums.PropertyStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyResponse {
    @NotNull
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private PropertyStatusEnum status;

    @NotNull
    private double price;

    @NotNull
    private Long brokerId;

    @NotNull
    private Long ownerId;
}
