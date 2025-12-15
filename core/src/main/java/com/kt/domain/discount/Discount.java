package com.kt.domain.discount;

import com.shop.domain.product.model.Product;
import com.shop.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Discount extends BaseEntity {

	private Long value;
	@Enumerated(EnumType.STRING)
	private DiscountType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	public Discount(Product product, Long value, DiscountType type) {
		this.product = product;
		this.value = value;
		this.type = type;
	}
}
