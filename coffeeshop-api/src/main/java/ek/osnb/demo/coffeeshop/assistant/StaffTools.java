package ek.osnb.demo.coffeeshop.assistant;

import ek.osnb.demo.coffeeshop.catalog.CatalogApi;
import ek.osnb.demo.coffeeshop.catalog.ProductDetailsInfo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

// Only given to staff by the controller
@Component
class StaffTools {

    private final CatalogApi catalog;

    StaffTools(CatalogApi catalog) {
        this.catalog = catalog;
    }

    @Tool(description = "Products that are running low: stock below the limit, lowest first")
    List<ProductDetailsInfo> lowStock(
            @ToolParam(description = "Stock limit, e.g. 10") int limit) {

        return catalog.searchProducts(null, null).stream()
                .filter(product -> product.stock() < limit)
                .sorted((a, b) -> Integer.compare(a.stock(), b.stock()))
                .toList();
    }

}
