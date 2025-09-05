package com.meli.technical.exam.api.products.domain.model;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class ProductComparison {

    private final Product product1;
    private final Product product2;

    public ProductComparison(Product product1, Product product2) {
        this.product1 = Objects.requireNonNull(product1, "First product cannot be null");
        this.product2 = Objects.requireNonNull(product2, "Second product cannot be null");
    }

    public Product getCheaperProduct() {
        return product1.getPrice().isLessThan(product2.getPrice()) ? product1 : product2;
    }

    public Product getMoreExpensiveProduct() {
        return product1.getPrice().isGreaterThan(product2.getPrice()) ? product1 : product2;
    }

    public Product getBetterRatedProduct() {
        return product1.getRating().isHigherThan(product2.getRating()) ? product1 : product2;
    }

    public Product getWorseRatedProduct() {
        return product1.getRating().isLowerThan(product2.getRating()) ? product1 : product2;
    }

    public boolean haveSamePrice() {
        return product1.getPrice().isEqualTo(product2.getPrice());
    }

    public boolean haveSameRating() {
        return product1.getRating().equals(product2.getRating());
    }

    public Price getPriceDifference() {
        var diff = product1.getPrice().getValue().subtract(product2.getPrice().getValue()).abs();
        return Price.of(diff);
    }

    public Set<String> getCommonSpecifications() {
        return intersect(product1.getSpecificationNames(), product2.getSpecificationNames());
    }

    public Set<String> getUniqueSpecificationsForProduct1() {
        return difference(product1.getSpecificationNames(), product2.getSpecificationNames());
    }

    public Set<String> getUniqueSpecificationsForProduct2() {
        return difference(product2.getSpecificationNames(), product1.getSpecificationNames());
    }

    public ComparisonResult getOverallComparison() {
        boolean cheaper = product1.getPrice().isLessThan(product2.getPrice());
        boolean better = product1.getRating().isHigherThan(product2.getRating());

        if (cheaper && better) return ComparisonResult.PRODUCT1_CLEARLY_BETTER;
        if (!cheaper && !better) return ComparisonResult.PRODUCT2_CLEARLY_BETTER;
        return ComparisonResult.TRADE_OFF_REQUIRED;
    }

    private Set<String> intersect(Set<String> a, Set<String> b) {
        return a.stream().filter(b::contains).collect(Collectors.toSet());
    }

    private Set<String> difference(Set<String> a, Set<String> b) {
        return a.stream().filter(spec -> !b.contains(spec)).collect(Collectors.toSet());
    }

}