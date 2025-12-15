package com.shop.discount.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.discount.Discount;
import com.shop.domain.discount.DiscountType;
import com.shop.repository.discount.DiscountRepository;
import com.shop.repository.product.ProductRepository;

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
