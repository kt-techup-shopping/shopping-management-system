package com.shop.repository.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.shop.domain.order.OrderStatus;
import com.shop.repository.order.response.AdminOrderDetailQueryResponse;
import com.shop.repository.order.response.AdminOrderDetailUserQueryResponse;
import com.shop.repository.order.response.OrderDetailQueryResponse;
import com.shop.repository.order.response.OrderDetailUserQueryResponse;

public interface OrderRepositoryCustom {
	// Page<OrderResponse.Search> search(String keyword, Pageable pageable);
	List<OrderDetailQueryResponse> findOrderDetailByUserId(Long userId);
	Page<AdminOrderDetailQueryResponse> findAdminOrderDetail(Long orderId, Long userId, OrderStatus from, PageRequest pageable);
	List<OrderDetailUserQueryResponse> findOrderDetailByUserIdAndOrderId(Long id, Long id1);
	List<AdminOrderDetailUserQueryResponse> findAdminOrderDetailUserById(Long orderId);
}
