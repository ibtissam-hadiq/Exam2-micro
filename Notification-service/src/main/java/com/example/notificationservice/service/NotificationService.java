package com.example.notificationservice.service;

import com.example.notificationservice.Repo.NotificationRepo;
import com.example.notificationservice.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepo notificationRepository;



    public Notification createNotification(String customerId, String orderId, String message, String type) {

        Notification notification = new Notification();
        notification.setCustomerId(customerId);
        notification.setOrderId(orderId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus("PENDING");
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public void sendNotification(Notification notification) {
        try {

            // Envoi de l'email
            emailService.sendEmail(
                    notification.getCustomerId(),
                    "Notification de commande",
                    notification.getMessage()
            );

            // Mise à jour du statut
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);

            log.info("Notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending notification", e);
            notification.setStatus("FAILED");
            notificationRepository.save(notification);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    public List<Notification> getCustomerNotifications(String customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }

    public List<Notification> getOrderNotifications(String orderId) {
        return notificationRepository.findByOrderId(orderId);
    }
}
