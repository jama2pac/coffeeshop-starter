package ek.osnb.demo.coffeeshop.assistant;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.client.ChatClient;

@RestController
@RequestMapping("/api/assistant")
class AssistantController {

    private final ChatClient chatClient;

    // Spring builds the builder from the spring.ai.openai settings in application.properties
    AssistantController(ChatClient.Builder builder) {
        this.chatClient = builder
        // The system prompt: rules the model gets before every message
        .defaultSystem("""
                        You are the assistant of a small coffee shop web shop.
                        Always answer in the same language as the user's last message.
                        Never make up products, prices or orders.
                        You can't change anything: no new or cancelled orders.
                        /no_think
                        """)
                .build();
    }

    // Logged in only: anyRequest().authenticated() in SecurityConfig covers it
    @PostMapping("/chat")
    ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        String reply = chatClient.prompt()
                .user(request.message())
                .call()
                .content();

        return new ChatResponse(reply);
    }
}
