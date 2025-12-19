package com.shop.repository.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.domain.product.Product;

import jakarta.persistence.LockModeType;

public interface ProductRepository extends ProductRepositoryCustom, JpaRepository<Product, Long> {
	default Product findByIdOrThrow(Long id) {
		return findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
	}

	default List<Product> findAllByIdOrThrow(List<Long> ids) {
		List<Product> products = findAllById(ids);

		if (products.size() != ids.size()) {
			throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
		}

		return products;
	}


	// select * from product where name = ?
	Optional<Product> findByName(String name);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM Product p WHERE p.id = :id")
	Optional<Product> findByIdPessimistic(Long id);

}
