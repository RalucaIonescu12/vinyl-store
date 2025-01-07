package com.example.vinylstore;

import com.example.vinylstore.CONTROLLERS.AuthController;
import com.example.vinylstore.MODELS.*;
import com.example.vinylstore.SERVICES.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class AuthControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private VinylService vinylService;

    @Mock
    private OrderService orderService;

    @Mock
    private ShippingAddressService addressService;

    @Mock
    private Model model;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowMainPage() {
        String view = authController.showMainPage();
        assertEquals("main", view);
    }

    @Test
    void testShowRegisterPage() {
        String view = authController.showRegisterPage();
        assertEquals("register", view);
    }

    @Test
    void testRegisterUser_Success() {
        String username = "testuser";
        String password = "password";

        doAnswer(invocation -> null).when(userService).registerUser(username, password);

        String view = authController.registerUser(username, password);

        assertEquals("redirect:/login", view);
        verify(userService, times(1)).registerUser(username, password);
    }

    @Test
    void testRegisterUser_Failure() {
        String username = "testuser";
        String password = "password";

        doThrow(new IllegalArgumentException("Error message")).when(userService).registerUser(username, password);

        String view = authController.registerUser(username, password);

        assertEquals("redirect:/register?error=Error message", view);
        verify(userService, times(1)).registerUser(username, password);
    }

    @Test
    void testShowLoginPage() {
        String view = authController.showLoginPage();
        assertEquals("login", view);
    }

    @Test
    void testLoginUser() {
        String view = authController.loginUser("testuser", "password");
        assertEquals("redirect:/vinyls", view);
    }



    @Test
    void testShowProfilePage() {
        User user = new User();
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        String view = authController.showProfilePage(model, userDetails);

        assertEquals("profile", view);
        verify(model).addAttribute("user", user);
    }

    @Test
    void testShowCartPage() {
        User user = new User();
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setTotalPrice(BigDecimal.ZERO);

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(orderService.getActiveCart(user)).thenReturn(order);

        String view = authController.showCartPage(model, userDetails);

        assertEquals("cart", view);
        verify(model).addAttribute("cartItems", order.getOrderItems());
        verify(model).addAttribute("totalPrice", order.getTotalPrice());
    }

    @Test
    void testSaveAddress() {
        ShippingAddress address = new ShippingAddress();
        User user = new User();

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        String view = authController.saveAddress(address, userDetails, model);

        assertEquals("fillShippingAddress", view);
        verify(addressService).saveAddress(address);
        verify(model).addAttribute("shippingAddress", address);
        verify(model).addAttribute("successMessage", "Address saved successfully!");
    }

    @Test
    void testCheckout() {
        User user = new User();
        Order order = new Order();
        order.setId(123L);

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(orderService.getActiveCart(user)).thenReturn(order);

        String view = authController.checkout(userDetails, model);

        assertEquals("redirect:/confirmation?orderNumber=123", view);
        verify(orderService).checkout(order);
    }

    @Test
    void testShowConfirmationPage() {
        String view = authController.showConfirmationPage(123L, model);

        assertEquals("confirmation", view);
        verify(model).addAttribute("orderNumber", 123L);
    }
}
