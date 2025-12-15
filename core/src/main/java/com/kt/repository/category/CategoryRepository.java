package com.kt.repository.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByParentId(Long parentId);

	default Category findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	List<Category> findByParentIsNull();
}
