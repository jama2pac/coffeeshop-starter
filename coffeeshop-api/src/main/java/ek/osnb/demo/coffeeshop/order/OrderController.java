package ek.osnb.demo.coffeeshop.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderService service;

    OrderController(OrderService service) {
        this.service = service;
    }

    // Checkout: turns a cart into an order
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponse placeOrder(Authentication authentication, @Valid @RequestBody PlaceOrderRequest request) {
        return service.placeOrder(authentication.getName(), request);
    }

    // GET /api/orders: only the logged-in customer's own orders, newest first
    @GetMapping
    List<OrderSummaryResponse> findMine(Authentication authentication) {

        String email = authentication.getName();
        return service.findMine(email);
    }

    @GetMapping("/{id}")
    OrderResponse findById(Authentication authentication, @PathVariable Long id) {
        return service.findById(id, authentication.getName(), isStaff(authentication));
    }

    @PostMapping("/{id}/cancel")
    OrderResponse cancel(Authentication authentication, @PathVariable Long id) {
        return service.cancel(id, authentication.getName());
    }

    // Should only be allowed for staff - a job for Spring Security later
    @PostMapping("/{id}/ship")
    OrderResponse ship(@PathVariable Long id) {
        return service.ship(id);
    }

    private boolean isStaff(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STAFF"));
    }
}
