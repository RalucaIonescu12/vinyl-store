package com.example.vinylstore.MODELS;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Use mappedBy to indicate that the "vinyls" relationship is managed by Vinyl class
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private String status;
    private BigDecimal totalPrice;

    @Column(name = "created_at", insertable = false, updatable = false)
    private java.sql.Timestamp createdAt;

    @Column(name = "updated_at")
    private java.sql.Timestamp updatedAt;

    // Getters and setters


    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    public List<Vinyl> getVinyls() {
        return orderItems.stream()
                .map(OrderItem::getVinyl)
                .collect(Collectors.toList());
    }

    public void addVinyl(Vinyl vinyl, int quantity, BigDecimal price) {
        // Verificați dacă vinyl-ul există deja în comandă
        OrderItem existingItem = orderItems.stream()
                .filter(item -> item.getVinyl().equals(vinyl))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Actualizați cantitatea și prețul
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPrice(existingItem.getPrice().add(price.multiply(BigDecimal.valueOf(quantity))));
        } else {
            // Adăugați un nou item în comandă
            OrderItem newItem = new OrderItem();
            newItem.setVinyl(vinyl);
            newItem.setQuantity(quantity);
            newItem.setPrice(price.multiply(BigDecimal.valueOf(quantity)));
            newItem.setOrder(this);
            orderItems.add(newItem);
        }
        updateTotalPrice();
    }
    public void removeVinyl(Long vinylId) {
        orderItems.removeIf(item -> item.getVinyl().getId().equals(vinylId));
    }
    public void removeVinylFromCart(Long vinylId) {
        orderItems.removeIf(item -> item.getVinyl().getId().equals(vinylId));
        updateTotalPrice();
    }
    private void updateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .map(item -> item.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
