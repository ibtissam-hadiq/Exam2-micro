package com.example.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;
    private String orderId;
    private String message;
    private String status;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

}
