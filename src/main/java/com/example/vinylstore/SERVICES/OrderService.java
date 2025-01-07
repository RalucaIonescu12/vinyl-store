package com.example.vinylstore.SERVICES;

import com.example.vinylstore.MODELS.Order;
import com.example.vinylstore.MODELS.OrderItem;
import com.example.vinylstore.MODELS.User;
import com.example.vinylstore.MODELS.Vinyl;
import com.example.vinylstore.REPOSITORIES.OrderRepository;
import com.example.vinylstore.REPOSITORIES.VinylRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final VinylRepository vinylRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository, VinylRepository vinylRepository) {
        this.orderRepository = orderRepository;
        this.vinylRepository = vinylRepository;
    }

    public Order getActiveCart(User user) {
        logger.info("Retrieving active cart for user: {}", user.getUsername());
        Optional<Order> existingOrder = orderRepository.findByUserAndStatus(user, "ACTIVE");

        if (existingOrder.isPresent()) {
            logger.info("Active order found for user: {}", user.getUsername());
            return existingOrder.get();
        } else {
            logger.info("No active order found for user: {}, creating a new one", user.getUsername());
            Order newOrder = new Order();
            newOrder.setUser(user);
            newOrder.setStatus("ACTIVE");
            newOrder.setOrderItems(new ArrayList<>());
            orderRepository.save(newOrder);
            logger.info("New active order created with ID: {} for user: {}", newOrder.getId(), user.getUsername());
            return newOrder;
        }
    }

    public void addVinylToOrder(Order order, Vinyl vinyl, int quantity, BigDecimal price) {
        logger.info("Adding vinyl with ID: {} to order ID: {}", vinyl.getId(), order.getId());
        try {
            order.addVinyl(vinyl, quantity, price);
            orderRepository.save(order);
            logger.info("Vinyl with ID: {} added successfully to order ID: {}", vinyl.getId(), order.getId());
        } catch (Exception e) {
            logger.error("Failed to add vinyl with ID: {} to order ID: {}. Error: {}", vinyl.getId(), order.getId(), e.getMessage());
            throw e;
        }
    }

    public void removeVinylFromCart(Order order, Long vinylId) {
        logger.info("Removing vinyl with ID: {} from order ID: {}", vinylId, order.getId());
        try {
            order.removeVinylFromCart(vinylId);
            orderRepository.save(order);
            logger.info("Vinyl with ID: {} removed successfully from order ID: {}", vinylId, order.getId());
        } catch (Exception e) {
            logger.error("Failed to remove vinyl with ID: {} from order ID: {}. Error: {}", vinylId, order.getId(), e.getMessage());
            throw e;
        }
    }

    public List<Vinyl> getAllVinylsFromOrder(Order order) {
        logger.info("Fetching all vinyls from order ID: {}", order.getId());
        List<Vinyl> vinyls = order.getVinyls();
        logger.info("Found {} vinyl(s) in order ID: {}", vinyls.size(), order.getId());
        return vinyls;
    }

    public void checkout(Order order) {
        logger.info("Checking out order ID: {}", order.getId());
        try {
            order.setStatus("COMPLETED");
            order.setTotalPrice(order.getOrderItems().stream()
                    .map(OrderItem::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            orderRepository.save(order);
            logger.info("Order ID: {} checked out successfully with total price: {}", order.getId(), order.getTotalPrice());
        } catch (Exception e) {
            logger.error("Failed to checkout order ID: {}. Error: {}", order.getId(), e.getMessage());
            throw e;
        }
    }

    private BigDecimal calculateTotalPrice(Order order) {
        logger.debug("Calculating total price for order ID: {}", order.getId());
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Vinyl> vinyls = order.getVinyls();
        for (Vinyl vinyl : vinyls) {
            totalPrice = totalPrice.add(vinyl.getPrice());
        }
        logger.debug("Total price for order ID: {} is: {}", order.getId(), totalPrice);
        return totalPrice;
    }
}
