package com.shop.domain.order.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.shop.domain.order.model.OrderStatus;
import com.shop.domain.order.response.AdminOrderDetailQueryResponse;
import com.shop.domain.order.response.AdminOrderDetailResponse;
import com.shop.domain.order.response.AdminOrderDetailUserQueryResponse;
import com.shop.domain.order.response.OrderDetailQueryResponse;
import com.shop.domain.order.response.OrderDetailUserQueryResponse;
import com.shop.domain.order.response.OrderResponse;

public interface OrderRepositoryCustom {
	Page<OrderResponse.Search> search(String keyword, Pageable pageable);
	List<OrderDetailQueryResponse> findOrderDetailByUserId(Long userId);
	Page<AdminOrderDetailQueryResponse> findAdminOrderDetail(Long orderId, Long userId, OrderStatus from, PageRequest pageable);
	List<OrderDetailUserQueryResponse> findOrderDetailByUserIdAndOrderId(Long id, Long id1);
	List<AdminOrderDetailUserQueryResponse> findAdminOrderDetailUser(Long orderId);
}
