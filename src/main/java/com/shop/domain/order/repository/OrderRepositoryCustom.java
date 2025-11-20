package com.shop.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shop.domain.order.response.OrderResponse;

public interface OrderRepositoryCustom {
	Page<OrderResponse.Search> search(String keyword, Pageable pageable);
}
