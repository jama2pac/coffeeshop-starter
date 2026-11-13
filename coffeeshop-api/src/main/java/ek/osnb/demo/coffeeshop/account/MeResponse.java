package ek.osnb.demo.coffeeshop.account;

import java.util.List;

// Svaret fra GET /api/me: {"username": "...", "roles": ["ROLE_CUSTOMER"]}
record MeResponse(
        String username,
        List<String> roles
) {
}
