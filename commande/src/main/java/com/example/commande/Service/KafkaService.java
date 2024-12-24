package com.example.commande.Service;

import com.example.commande.entite.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaService {
    @Autowired
    private ObjectMapper objectMapper;

    public void sendOrderNotification(Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("order-notifications", orderJson);
        } catch (Exception e) {
            throw new RuntimeException("Erreur Kafka", e);
        }
    }
