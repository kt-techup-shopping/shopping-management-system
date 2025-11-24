package com.shop.domain.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.category.service.CategoryService;
import com.shop.domain.product.dto.response.ProductSearchResponse;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryService categoryService;

	public void create(String name, Long price, Long quantity) {
		productRepository.save(
			new Product(
				name,
				price,
				quantity
			)
		);
	}

	public void update(Long id, String name, Long price, Long quantity) {
		var product = productRepository.findByIdOrThrow(id);

		product.update(name, price, quantity);
	}

	public void soldOut(Long id) {
		var product = productRepository.findByIdOrThrow(id);

		product.soldOut();
	}

	public void inActivate(Long id) {
		var product = productRepository.findByIdOrThrow(id);

		product.inActivate();
	}

	public void activate(Long id) {
		var product = productRepository.findByIdOrThrow(id);

		product.activate();
	}

	public void delete(Long id) {
		var product = productRepository.findByIdOrThrow(id);

		product.delete();
	}

	public void decreaseStock(Long id, Long quantity) {
		var product = productRepository.findByIdOrThrow(id);

		product.decreaseStock(quantity);
	}

	public void increaseStock(Long id, Long quantity) {
		var product = productRepository.findByIdOrThrow(id);

		product.increaseStock(quantity);
	}

	// 상품 목록 조회
	public Page<ProductSearchResponse> search(String keyword, Long categoryId, PageRequest pageable) {
		return productRepository.search(keyword, categoryId, pageable);
	}


}
