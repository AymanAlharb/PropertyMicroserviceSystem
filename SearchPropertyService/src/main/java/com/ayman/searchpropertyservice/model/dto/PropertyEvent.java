package com.ayman.searchpropertyservice.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyEvent {
    private Long id;
    private String title;
    private String description;
    private double price;
}
