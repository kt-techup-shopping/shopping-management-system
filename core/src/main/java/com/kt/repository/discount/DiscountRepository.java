package com.kt.repository.discount;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.discount.model.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
