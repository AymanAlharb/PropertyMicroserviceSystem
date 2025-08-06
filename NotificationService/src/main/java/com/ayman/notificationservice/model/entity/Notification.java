package com.ayman.notificationservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(256) not null")
    private String message;

    @Column(columnDefinition = "date not null")
    private LocalDateTime date;

    private Long receiverId;
}
