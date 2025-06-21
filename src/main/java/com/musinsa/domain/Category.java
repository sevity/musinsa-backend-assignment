// domain/Category.java
package com.musinsa.domain;

import lombok.Getter;

@Getter
public enum Category {
    TOP("상의"),
    OUTER("아우터"),
    BOTTOM("바지"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    HAT("모자"),
    SOCKS("양말"),
    ACCESSORY("액세서리");

    private final String krName;
    Category(String krName) { this.krName = krName; }

    public static Category fromKr(String kr) {
        if (kr == null) {
            throw new IllegalArgumentException("Category is null");
        }
        for (Category c : values()) if (c.krName.equals(kr)) return c;
        throw new IllegalArgumentException("Unknown category: " + kr);
    }
}
