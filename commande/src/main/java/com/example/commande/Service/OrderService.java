package com.example.commande.Service;

import com.example.commande.Repo.OrderRepo;
import com.example.commande.entite.Order;
import com.example.commande.entite.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Transactional

    public Order createOrder(Order order) {


        validateAndUpdateStock(order.getItems());

        // Initialiser les détails de la commande
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CREATED");
        calculateTotalAmount(order);

        // Sauvegarder la commande
        Order savedOrder = orderRepository.save(order);

        // Envoyer notification via Kafka
        kafkaProducerService.sendOrderNotification(savedOrder);

        return savedOrder;
    }

    private void validateAndUpdateStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            boolean stockUpdated = productServiceClient.updateStock(
                    item.getProductId(),
                    -item.getQuantity()
            );

            if (!stockUpdated) {
                throw new RuntimeException("Stock insuffisant pour le produit: " + item.getProductId());
            }
        }
    }

    private void calculateTotalAmount(Order order) {
        double total = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(total);
    }

    public Order createOrderFallback(Order order, Exception e) {
        throw new RuntimeException("Service produit temporairement indisponible");
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée: " + id));
    }

    @Transactional
    public Order updateOrderStatus(Long id, String newStatus) {
        Order order = getOrderById(id);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

}
