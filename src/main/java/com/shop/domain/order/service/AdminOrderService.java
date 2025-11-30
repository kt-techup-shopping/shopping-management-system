package com.shop.domain.order.service;

import org.springframework.stereotype.Service;

import com.shop.domain.order.model.OrderStatus;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.order.request.AdminOrderStatusChangeRequest;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

	private final OrderRepository orderRepository;

	public void updateOrderStatus(AdminOrderStatusChangeRequest adminOrderStatusChangeRequest, Long orderId){
		var order = orderRepository.findByIdAndIsDeletedFalse(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

		order.updateStatus(adminOrderStatusChangeRequest.orderStatus());
	}

	public void deleteOrder(Long orderId){
		var order = orderRepository.findByIdAndIsDeletedFalse(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

		order.updateStatus(OrderStatus.CANCELLED);
	}
}
