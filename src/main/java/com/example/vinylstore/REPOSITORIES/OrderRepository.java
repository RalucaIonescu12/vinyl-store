package com.example.vinylstore.REPOSITORIES;

import com.example.vinylstore.MODELS.Order;
import com.example.vinylstore.MODELS.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findAll();
    Order findById(Long id);
    Optional<Order> findByUserAndStatus(User user, String status);
}
