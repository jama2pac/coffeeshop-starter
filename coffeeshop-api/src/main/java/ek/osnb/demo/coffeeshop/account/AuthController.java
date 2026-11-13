package ek.osnb.demo.coffeeshop.account;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
class AuthController {

    private final AccountService service;

    AuthController(AccountService service) {
        this.service = service;
    }

    // Frontend logger selv ind bagefter, så her oprettes kun kontoen
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    void register(@Valid @RequestBody RegisterRequest request) {
        service.register(request);
    }

    // Kun indloggede når hertil: SecurityFilterChain svarer 401 for alle andre
    @GetMapping("/me")
    MeResponse me(Authentication authentication) {

        String username = authentication.getName();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();

        return new MeResponse(username, roles);
    }
}
