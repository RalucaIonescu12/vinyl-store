package com.example.vinylstore.CONTROLLERS;

import com.example.vinylstore.MODELS.Order;
import com.example.vinylstore.MODELS.ShippingAddress;
import com.example.vinylstore.MODELS.User;
import com.example.vinylstore.MODELS.Vinyl;
import com.example.vinylstore.SERVICES.OrderService;
import com.example.vinylstore.SERVICES.ShippingAddressService;
import com.example.vinylstore.SERVICES.UserService;
import com.example.vinylstore.SERVICES.VinylService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final VinylService vinylService;
    private final OrderService orderService;
    private ShippingAddressService addressService;

    @Autowired
    public AuthController(UserService userService, VinylService vinylService, OrderService orderService, ShippingAddressService shippingAddressService) {
        this.userService = userService;
        this.vinylService = vinylService;
        this.orderService = orderService;
        this.addressService=shippingAddressService;
    }

    @GetMapping("/")
    public String showMainPage() {
        logger.info("Main page accessed.");
        return "main";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        logger.info("Register page accessed.");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        logger.info("Attempting to register user with username: {}", username);
        try {
            userService.registerUser(username, password);
            logger.info("User registered successfully: {}", username);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            logger.error("Registration failed for user {}: {}", username, e.getMessage());
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        logger.info("Login page accessed.");
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        logger.info("User logged in with username: {}", username);
        return "redirect:/vinyls";
    }

    @GetMapping("/vinyls")
    public String showVinylsPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Vinyls page accessed by user: {}", userDetails.getUsername());
        List<Vinyl> vinyls = vinylService.getAllVinyls();
        model.addAttribute("vinyls", vinyls);
        model.addAttribute("isAdmin", userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        logger.debug("Vinyls loaded: {}", vinyls);
        return "vinyls";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Profile page accessed by user: {}", userDetails.getUsername());
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        logger.debug("User profile data: {}", user);
        return "profile";
    }

    @PostMapping("/admin/increase-stock/{vinylId}")
    public String increaseStock(@PathVariable Long vinylId, @RequestParam int quantity, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Increase stock requested by admin: {} for vinyl ID: {} with quantity: {}", userDetails.getUsername(), vinylId, quantity);
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            logger.warn("Unauthorized stock increase attempt by user: {}", userDetails.getUsername());
            throw new SecurityException("Access denied: only admins can modify stock!");
        }
        vinylService.increaseStock(vinylId, quantity);
        logger.info("Stock increased for vinyl ID: {} by quantity: {}", vinylId, quantity);
        return "redirect:/vinyls";
    }

    @GetMapping("/cart")
    public String showCartPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            logger.info("Cart page accessed by user: {}", userDetails.getUsername());
            User user = userService.getUserByUsername(userDetails.getUsername());
            Order order = orderService.getActiveCart(user);
            model.addAttribute("cartItems", order.getOrderItems());
            model.addAttribute("totalPrice", order.getTotalPrice());
            logger.debug("Cart items: {} | Total price: {}", order.getOrderItems(), order.getTotalPrice());
        } else {
            logger.warn("Cart page accessed without user authentication.");
        }
        return "cart";
    }


    @PostMapping("/cart/remove/{vinylId}")
    public String removeFromCart(@PathVariable Long vinylId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            logger.info("Removing vinyl ID: {} from cart for user: {}", vinylId, userDetails.getUsername());
            User user = userService.getUserByUsername(userDetails.getUsername());
            Order order = orderService.getActiveCart(user);
            orderService.removeVinylFromCart(order, vinylId);
            logger.info("Vinyl ID: {} removed from cart for user: {}", vinylId, userDetails.getUsername());
        } else {
            logger.warn("Remove from cart request without user authentication.");
        }
        return "redirect:/cart";
    }


    @GetMapping("admin/add-vinyl-form")
    public String showAddVinylForm(Model model) {
        model.addAttribute("vinyl", new Vinyl());
        return "addVinylForm";
    }

    @PostMapping("admin/add-vinyl")
    public String addVinyl(@ModelAttribute Vinyl vinyl) {
        vinylService.addVinyl(vinyl);
        return "redirect:/vinyls";
    }

    @PostMapping("admin/remove-vinyl/{vinylId}")
    public String removeVinyl(@PathVariable Long vinylId) {
        vinylService.removeVinyl(vinylId);
        return "redirect:/vinyls";
    }
    @PostMapping("/cart/shipping-address/save")
    public String saveAddress(ShippingAddress shippingAddress, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userService.getUserByUsername(userDetails.getUsername());
            shippingAddress.setUser(user);
            addressService.saveAddress(shippingAddress);
        }
        model.addAttribute("shippingAddress", shippingAddress);
        model.addAttribute("successMessage", "Address saved successfully!");

        return "fillShippingAddress";
    }
    @PostMapping("/cart/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userService.getUserByUsername(userDetails.getUsername());
            Order activeOrder = orderService.getActiveCart(user);
            orderService.checkout(activeOrder);
            return "redirect:/confirmation?orderNumber=" + activeOrder.getId();
        }
       return "redirect:/";
    }
    @RequestMapping(value = "/cart/fill-shipping-address", method = {RequestMethod.GET, RequestMethod.POST})
    public String fillShippingAddress(Model model) {
        model.addAttribute("shippingAddress", new ShippingAddress());
        return "fillShippingAddress";
    }
    @GetMapping("/confirmation")
    public String showConfirmationPage(@RequestParam(required = false) Long orderNumber, Model model) {
        model.addAttribute("orderNumber", orderNumber);
        return "confirmation";
    }

    @PostMapping("/cart/add/{vinylId}")
    public String addToCart(@PathVariable Long vinylId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            logger.info("Adding vinyl ID: {} to cart for user: {}", vinylId, userDetails.getUsername());
            User user = userService.getUserByUsername(userDetails.getUsername());
            Order order = orderService.getActiveCart(user);
            Vinyl vinyl = vinylService.getVinylById(vinylId);
            orderService.addVinylToOrder(order, vinyl, 1, vinyl.getPrice());
            logger.info("Vinyl ID: {} added to cart for user: {}", vinylId, userDetails.getUsername());
        } else {
            logger.warn("Add to cart request without user authentication.");
        }
        return "redirect:/cart";
    }
}
