package ek.osnb.demo.coffeeshop.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Staff only (SecurityConfig): every customer's orders, newest first
@RestController
@RequestMapping("/api/staff/orders")
class StaffOrderController {

    private final OrderService orderService;

    StaffOrderController(OrderService service) {
        this.orderService = service;
    }

    @GetMapping
    List<OrderSummaryResponse> findAll() {
        return orderService.findAll();
    }
}
