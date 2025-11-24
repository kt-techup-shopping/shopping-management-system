package com.shop.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByParentId(Long parentId);
}
