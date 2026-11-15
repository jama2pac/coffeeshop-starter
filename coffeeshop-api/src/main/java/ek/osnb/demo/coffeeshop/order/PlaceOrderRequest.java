package ek.osnb.demo.coffeeshop.order;

import jakarta.validation.constraints.NotNull;

// Only cartId: the customer comes from the login, so no one can order in someone else's name
record PlaceOrderRequest(

        @NotNull
        Long cartId
) {
}
