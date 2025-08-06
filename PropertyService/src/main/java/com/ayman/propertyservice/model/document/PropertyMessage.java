package com.ayman.propertyservice.model.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyMessage {
    private Long id;
    private String title;
    private String description;
    private double price;

}
