package com.example.vinylstore.CONTROLLERS;

import com.example.vinylstore.MODELS.Order;
import com.example.vinylstore.MODELS.User;
import com.example.vinylstore.MODELS.Vinyl;
import com.example.vinylstore.SERVICES.OrderService;
import com.example.vinylstore.SERVICES.UserService;
import com.example.vinylstore.SERVICES.VinylService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class RestController {
    private final UserService userService;
    private final VinylService vinylService;
    private final OrderService orderService;

    public RestController(UserService userService, VinylService vinylService, OrderService orderService) {
        this.userService = userService;
        this.vinylService = vinylService;
        this.orderService = orderService;
    }

    @Operation(summary = "Creeaza un utilizator nou")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String username, @RequestParam String password) {
        try {
            userService.registerUser(username, password);
            return ResponseEntity.ok("User registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtine profilul utilizatorului autentificat")
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Obtine lista de viniluri")
    @GetMapping("/vinyls")
    public ResponseEntity<List<Vinyl>> getAllVinyls(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(null); // No authenticated user
        }

        System.out.println("Authenticated user: " + userDetails.getUsername());
        List<Vinyl> vinyls = vinylService.getAllVinyls();
        return ResponseEntity.ok(vinyls);
    }
    @Operation(summary = "Obtine vinyl-rile din cos.")
    @GetMapping("/cart")
    public ResponseEntity<Order> showCart(@AuthenticationPrincipal UserDetails userDetails) {
            User user = userService.getUserByUsername(userDetails.getUsername());
            Order order = orderService.getActiveCart(user);
        return ResponseEntity.ok(order);
    }
    @Operation(summary = "Adauga un vinil în cos")
    @PostMapping("/cart/add/{vinylId}")
    public ResponseEntity<String> addToCart(@PathVariable Long vinylId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("User not authenticated.");
        }
        User user = userService.getUserByUsername(userDetails.getUsername());
        Order order = orderService.getActiveCart(user);
        Vinyl vinyl = vinylService.getVinylById(vinylId);
        orderService.addVinylToOrder(order, vinyl, 1, vinyl.getPrice());
        return ResponseEntity.ok("Vinyl added to cart.");
    }
}
