package com.shop.domain.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import com.shop.BaseEntity;
import com.shop.ErrorCode;
import com.shop.Preconditions;

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

	public List<Category> getHierarchy() {
		List<Category> list = new ArrayList<>();
		Category current = this;

		while (current != null) {
			list.add(current);
			current = current.getParent();
		}

		Collections.reverse(list);
		return list;
	}
}
