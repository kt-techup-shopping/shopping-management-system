package com.shop.domain.order.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shop.domain.order.response.OrderDetailQueryResponse;
import com.shop.domain.order.response.OrderResponse;

public interface OrderRepositoryCustom {
	Page<OrderResponse.Search> search(String keyword, Pageable pageable);
	List<OrderDetailQueryResponse> findOrderDetailByUserId(Long userId);
}
