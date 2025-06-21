package com.musinsa.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Test
    void fromKr_nullInput_throwsException() {
        assertThatThrownBy(() -> Category.fromKr(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category is null");
    }
}
