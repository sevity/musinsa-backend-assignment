// domain/Brand.java
package com.musinsa.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity
public class Brand {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    public Brand(String name) { this.name = name; }
}
