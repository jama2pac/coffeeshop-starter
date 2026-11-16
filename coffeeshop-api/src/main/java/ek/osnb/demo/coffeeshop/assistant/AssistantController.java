package ek.osnb.demo.coffeeshop.assistant;

import jakarta.validation.Valid;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.Authentication;

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
                // Memory: the last 10 messages of each conversation are sent along with every new message
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder().maxMessages(10).build()).build())
                .build();
    }

    // Logged in only: anyRequest().authenticated() in SecurityConfig covers it
    @PostMapping("/chat")
    ChatResponse chat(Authentication authentication, @Valid @RequestBody ChatRequest request) {
        String reply = chatClient.prompt()
                .user(request.message())
                // One conversation per user: the email from the login is the conversation id
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, authentication.getName()))
                .call()
                .content();

        return new ChatResponse(reply);
    }
}
