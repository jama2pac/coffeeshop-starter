package ek.osnb.demo.coffeeshop.customer;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
class CustomerController {

    private final CustomerService service;

    CustomerController(CustomerService service) {
        this.service = service;
    }

    // Email comes from the login, not the URL, so no one can read another customer's profile
    @GetMapping("/me")
    CustomerResponse findMine(Authentication authentication) {

        String email = authentication.getName();

        return service.findMine(email);
    }

    // One PUT for both create and update, so the frontend doesn't need to know if the profile exists.
    @PutMapping("/me")
    CustomerResponse saveMine(Authentication authentication, @Valid @RequestBody CustomerRequest request) {
        return service.saveMine(authentication.getName(), request);
    }
}
