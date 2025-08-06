package com.ayman.propertyservice.model.entity;

import com.ayman.propertyservice.model.enums.PropertyStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(20) not null")
    private String title;

    @Column(columnDefinition = "varchar(256) not null")
    private String description;

    @Column(columnDefinition = "varchar(10) not null")
    @Enumerated(EnumType.STRING)
    private PropertyStatusEnum status;

    @Column(columnDefinition = "varchar(96) not null")
    private String city;

    @Column(columnDefinition = "varchar(96) not null")
    private String region;

    @Column(columnDefinition = "varchar(256) not null")
    private String location;

    @Column(columnDefinition = "double not null")
    private double price;

    private Long brokerId;

    private Long ownerId;
}