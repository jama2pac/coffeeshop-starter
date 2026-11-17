package ek.osnb.demo.coffeeshop.assistant;

import ek.osnb.demo.coffeeshop.order.OrderApi;
import ek.osnb.demo.coffeeshop.order.OrderInfo;
import ek.osnb.demo.coffeeshop.order.OrderSummaryInfo;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

// The customer's own order. The customer id comes from the ToolContext (set by the
// controller from the login), never from the model, so it can't read someone else's orders
@Component
class OrderTools {

    private final OrderApi orders;

    OrderTools(OrderApi orders) {
        this.orders = orders;
    }

    @Tool(description = "List the customer's own orders, newest first")
    List<OrderSummaryInfo> myOrders(ToolContext context) {
        return orders.findOrders(customerId(context));
    }

    @Tool(description = "One of the customer's own orders, with its lines and delivery address")
    OrderInfo myOrder(@ToolParam(description = "the order number") Long orderId, ToolContext context) {
        // OrderApi gives 404 for someone else's order, so a guessed number reveals nothing
        return orders.getOrder(customerId(context), orderId);
    }

    private Long customerId(ToolContext context) {
        return (Long) context.getContext().get("customerId");
    }
}
