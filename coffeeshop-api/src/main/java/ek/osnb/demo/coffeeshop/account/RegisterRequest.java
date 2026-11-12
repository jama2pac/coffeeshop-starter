package ek.osnb.demo.coffeeshop.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record RegisterRequest(

        @NotBlank @Email @Size(max = 254) String email,

        // BCrypt bruger kun de første 72 bytes af et password
        @NotBlank @Size(min = 8, max = 72) String password) {
}
