package com.shop.repository.discount;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.discount.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
