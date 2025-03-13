package com.kass.util.filters;

import org.springframework.data.jpa.domain.Specification;

import com.kass.models.Product;

public class ProductSpecification {
    
    public static Specification<Product> nameContains(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
    
    public static Specification<Product> priceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            } else if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            } else if (maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
        };
    }
}