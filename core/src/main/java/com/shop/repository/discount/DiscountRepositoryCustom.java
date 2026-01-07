package com.shop.repository.discount;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.shop.domain.product.ProductSort;
import com.shop.repository.discount.response.LatestDiscountQueryResponse;
import com.shop.repository.product.response.AdminProductDetailQueryResponse;
import com.shop.repository.product.response.AdminProductSearchQueryResponse;
import com.shop.repository.product.response.AdminProductStockQueryResponse;
import com.shop.repository.product.response.ProductDetailQueryResponse;
import com.shop.repository.product.response.ProductSearchQueryResponse;

public interface DiscountRepositoryCustom {
	List<LatestDiscountQueryResponse> fetchLatestDiscountsByProductIds(List<Long> productIds);
}
