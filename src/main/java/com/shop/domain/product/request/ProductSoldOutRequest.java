package com.shop.domain.product.request;

import java.util.List;

public record ProductSoldOutRequest(
	List<Long> productIds
) {
}
