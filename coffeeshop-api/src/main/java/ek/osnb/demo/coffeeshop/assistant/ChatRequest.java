package ek.osnb.demo.coffeeshop.assistant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// What the frontend sends: {message}
record ChatRequest(
            @NotBlank
            @Size(max = 2000)
            String message
){
}

