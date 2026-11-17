package ek.osnb.demo.coffeeshop.assistant;

import ek.osnb.demo.coffeeshop.customer.CustomerApi;
import ek.osnb.demo.coffeeshop.customer.CustomerInfo;
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

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/assistant")
class AssistantController {

    private final ChatClient chatClient;
    private final CustomerApi customers;
    private final CatalogTools catalogTools;
    private final OrderTools orderTools;
    private final StaffTools staffTools;

    // Spring builds the builder from the spring.ai.openai settings in application.properties
    AssistantController(ChatClient.Builder builder, CustomerApi customers, CatalogTools catalogTools, OrderTools orderTools, StaffTools staffTools) {
        this.customers = customers;
        this.catalogTools = catalogTools;
        this.orderTools = orderTools;
        this.staffTools = staffTools;

        this.chatClient = builder
                // The system prompt: rules the model gets before every message
                .defaultSystem("""
                        You are the assistant of a small coffee shop web shop.
                        Always answer in the same language as the user's last message.
                        Use the tools to look up products and orders. Only tell facts the tools gave you.
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
        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt()
                .user(request.message())
                // One conversation per user: the email from the login is the conversation id
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, authentication.getName()));

        if (isStaff(authentication)) {
            prompt.tools(catalogTools, staffTools);
        } else {
            // The customer id comes from the login, never from the model
            Optional<CustomerInfo> customer = customers.findCustomerByEmail(authentication.getName());

            if (customer.isPresent()) {
                prompt.tools(catalogTools, orderTools)
                        .toolContext(Map.of("customerId", customer.get().id()));
            } else {
                // No profile yet means no orders, and Map.of can't hold a null id
                prompt.tools(catalogTools);
            }
        }
        return new ChatResponse(prompt.call().content());
    }

    private boolean isStaff(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STAFF"));
    }
}
