package com.example.notificationservice.Repo;

import com.example.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification,Long> {
    List<Notification> findByCustomerId(String customerId);
    List<Notification> findByOrderId(String orderId);
}
