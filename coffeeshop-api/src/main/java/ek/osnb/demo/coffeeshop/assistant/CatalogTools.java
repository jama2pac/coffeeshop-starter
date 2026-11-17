package ek.osnb.demo.coffeeshop.assistant;

import ek.osnb.demo.coffeeshop.catalog.CatalogApi;
import ek.osnb.demo.coffeeshop.catalog.ProductDetailsInfo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

// Product lookups for everyone: only reads the catalog, never changes it
@Component
class CatalogTools {

    private final CatalogApi catalog;

    CatalogTools(CatalogApi catalog) {
        this.catalog = catalog;
    }

    @Tool(description = "Search the shop's products. both filters are optional. Returns name, category, origin, roast level, price and stock.")
    List<ProductDetailsInfo> searchProducts(
            @ToolParam(description = "Part of the name or origin, e.g. 'kenya'. Leave empty for all products.", required = false)
            String text,
            @ToolParam(description = "LIGHT, MEDIUM or DARK. Leave empty for any roast.", required = false)
            String roastLevel) {

        // CatalogApi can't filter by roast level, so we filter here: otherwise "light roasts" finds nothing
        return catalog.searchProducts(null, text).stream()
                .filter(product -> roastLevel == null || roastLevel.isBlank()
                || roastLevel.equalsIgnoreCase(product.roastLevel()))
        .toList();
    }
}
