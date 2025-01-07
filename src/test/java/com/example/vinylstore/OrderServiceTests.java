package com.example.vinylstore;

import com.example.vinylstore.MODELS.*;
import com.example.vinylstore.REPOSITORIES.OrderRepository;
import com.example.vinylstore.SERVICES.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getActiveCart_CreatesNewCartIfNoneExists() {
        User user = new User();
        user.setUsername("testuser");

        when(orderRepository.findByUserAndStatus(user, "ACTIVE")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.getActiveCart(user);

        assertNotNull(order);
        assertEquals("ACTIVE", order.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getActiveCart_ReturnsExistingCart() {
        User user = new User();
        user.setUsername("testuser");

        Order existingOrder = new Order();
        existingOrder.setStatus("ACTIVE");

        when(orderRepository.findByUserAndStatus(user, "ACTIVE")).thenReturn(Optional.of(existingOrder));

        Order order = orderService.getActiveCart(user);

        assertEquals("ACTIVE", order.getStatus());
        verify(orderRepository, never()).save(any(Order.class));
    }
}