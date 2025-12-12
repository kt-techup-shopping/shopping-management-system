package com.shop.domain.category.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import com.shop.domain.product.model.Product;
import com.shop.global.common.BaseEntity;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Category extends BaseEntity {

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_category_id")
	private Category parent;

	@OneToMany(mappedBy = "parent")
	private List<Category> children = new ArrayList<>();

	// @OneToMany(mappedBy = "category")
	// private List<Product> products = new ArrayList<>();

	public Category(String name, Category parent) {
		Preconditions.validate(Strings.isNotBlank(name), ErrorCode.INVALID_PARAMETER);

		this.name = name;
		this.parent = parent;
	}
}
