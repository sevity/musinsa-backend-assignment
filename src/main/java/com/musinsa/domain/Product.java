// domain/Product.java
package com.musinsa.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"brand_id", "category"}),
    indexes = @Index(name = "idx_product_category", columnList = "category")
)
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "brand_id")
    @JsonIgnoreProperties({"products", "hibernateLazyInitializer", "handler"})
    private Brand brand;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Category category;

    private int price; // 원 단위

    public Product(Brand brand, Category category, int price) {
        this.brand = brand;
        this.category = category;
        this.price = price;
    }
}
