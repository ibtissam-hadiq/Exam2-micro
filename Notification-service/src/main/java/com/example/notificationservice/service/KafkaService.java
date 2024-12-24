package com.example.notificationservice.service;

import com.example.notificationservice.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "order-notifications", groupId = "notification-group")
    public void handleOrderNotification(String orderJson) {
        try {

            // Convertir le JSON en objet Order
            OrderEvent orderEvent = objectMapper.readValue(orderJson, OrderEvent.class);

            // Créer et envoyer la notification
            String message = createNotificationMessage(orderEvent);
            Notification notification = notificationService.createNotification(
                    orderEvent.getCustomerId(),
                    orderEvent.getOrderId(),
                    message,
                    "ORDER_CREATED"
            );

            notificationService.sendNotification(notification);
        } catch (Exception e) {
            log.error("Error processing order notification", e);
        }
    }

    private String createNotificationMessage(OrderEvent orderEvent) {
        return String.format(
                "Votre commande #%s a été créée avec succès. Montant total : %.2f€",
                orderEvent.getOrderId(),
                orderEvent.getTotalAmount()
        );
    }
}
