package com.shop.domain.product.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import com.shop.domain.category.model.Category;
import com.shop.domain.discount.model.Discount;
import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.global.common.BaseEntity;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

	private String name;
	private Long price;
	private Long stock;
	private Long discountPrice;
	@Enumerated(EnumType.STRING)
	private ProductStatus status = ProductStatus.ACTIVATED;
	private String description;
	private String color;

	// @Version
	// private Long version;

	@OneToMany(mappedBy = "product")
	private List<OrderProduct> orderProducts = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "product")
	private List<Discount> discounts = new ArrayList<>();

	public Product(String name, Long price, Long stock, String description, String color, Category category) {
		Preconditions.validate(Strings.isNotBlank(name), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(Strings.isNotBlank(description), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(Strings.isNotBlank(color), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(price >= 0, ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(stock >= 0, ErrorCode.INVALID_PARAMETER);

		this.name = name;
		this.price = price;
		this.stock = stock;
		this.description = description;
		this.color = color;
		this.category = category;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Product(String name, Long price, Long stock) {
		Preconditions.validate(Strings.isNotBlank(name), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(price >= 0, ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(stock >= 0, ErrorCode.INVALID_PARAMETER);

		this.name = name;
		this.price = price;
		this.stock = stock;
	}

	public void update(
		String name,
		Long price,
		String description,
		String color,
		Long deltaStock,
		ProductStatus status,
		Category category
	) {
		Preconditions.validate(Strings.isNotBlank(name), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(Strings.isNotBlank(description), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(Strings.isNotBlank(color), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(price >= 0, ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(this.stock + deltaStock >= 0, ErrorCode.INVALID_PARAMETER);

		this.name = name;
		this.price = price;
		this.description = description;
		this.color = color;
		this.stock += deltaStock;
		this.status = status;
		this.category = category;
		this.updatedAt = LocalDateTime.now();
	}

	public void soldOut() {
		this.status = ProductStatus.SOLD_OUT;
	}

	public void inActivate() {
		this.status = ProductStatus.IN_ACTIVATED;
	}

	public void activate() {
		this.status = ProductStatus.ACTIVATED;
	}

	public void delete() {
		// 논리삭제
		this.status = ProductStatus.DELETED;
	}

	public void decreaseStock(Long quantity) {
		this.stock -= quantity;
	}

	public void increaseStock(Long quantity) {
		this.stock += quantity;
	}

	public boolean canProvide(Long quantity) {
		return this.stock >= quantity;
	}

	public void mapToOrderProduct(OrderProduct orderProduct) {
		this.orderProducts.add(orderProduct);
	}

	public boolean isActive() {
		return this.status == ProductStatus.ACTIVATED;
	}

	public boolean isInActive() {
		return this.status == ProductStatus.IN_ACTIVATED;
	}

	public boolean isSoldOut() {
		return stock == 0 && this.status == ProductStatus.SOLD_OUT;
	}

	public void getDiscountPrice(Long discountPrice) {
		this.price = discountPrice;
	}

	public void toggleSoldOut() {
		this.status = this.status == ProductStatus.SOLD_OUT
			? ProductStatus.ACTIVATED
			: ProductStatus.SOLD_OUT;
	}
}
