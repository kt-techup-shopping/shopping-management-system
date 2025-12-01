package com.shop.domain.discount.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.discount.model.Discount;
import com.shop.domain.discount.model.DiscountType;
import com.shop.domain.discount.repository.DiscountRepository;
import com.shop.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountService {

	private final DiscountRepository discountRepository;
	private final ProductRepository productRepository;

	// 관리자 상품 할인 등록
	@Transactional
	public void createDiscount(Long productId, Long value, DiscountType type) {
		var product = productRepository.findByIdOrThrow(productId);
		discountRepository.save(
			new Discount(
				product,
				value,
				type
			)
		);
	}
}
