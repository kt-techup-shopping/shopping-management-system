package com.shop.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.category.model.Category;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByParentId(Long parentId);

	default Category findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	// 최상위 카테고리 모두 조회 (parent = null)
	List<Category> findByParentIsNullOrderByIdAsc();

	// 특정 부모 ID 기준 자식 조회
	List<Category> findByParentIdOrderByIdAsc(Long parentId);

	List<Category> findByParentIsNull();
}
