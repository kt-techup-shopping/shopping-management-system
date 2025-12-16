package com.shop.delivery.service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.Lock;
import com.shop.delivery.request.DeliveryReadyRequest;
import com.shop.delivery.response.DeliveryResponse;
import com.shop.domain.delivery.Delivery;
import com.shop.repository.delivery.DeliveryRepository;
import com.shop.repository.order.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminDeliveryService {

	private final OrderRepository orderRepository;
	private final DeliveryRepository deliveryRepository;

	// Pending 상태로 변경하는 APi
	@Lock(key = Lock.Key.ORDER, index = 0, waitTime = 1000, leaseTime = 500, timeUnit = TimeUnit.MILLISECONDS)
	public DeliveryResponse updateToPending(Long orderId) {
		Delivery delivery = getDeliveryByOrderId(orderId);
		delivery.updatePending();
		return DeliveryResponse.from(delivery);
	}

	// Ready 상태로 변경하는 API
	@Lock(key = Lock.Key.ORDER, index = 0, waitTime = 1000, leaseTime = 500, timeUnit = TimeUnit.MILLISECONDS)
	public DeliveryResponse updateToReady(Long orderId, DeliveryReadyRequest request) {
		Delivery delivery = getDeliveryByOrderId(orderId);
		delivery.updateReady(request.waybillNo());
		return DeliveryResponse.from(delivery);
	}

	// Shipping 상태로 변경하는 API
	@Lock(key = Lock.Key.ORDER, index = 0, waitTime = 1000, leaseTime = 500, timeUnit = TimeUnit.MILLISECONDS)
	public DeliveryResponse updateToShipping(Long orderId) {
		Delivery delivery = getDeliveryByOrderId(orderId);
		delivery.updateShipping();
		return DeliveryResponse.from(delivery);
	}

	// Delivered 상태로 변경하는 API
	@Lock(key = Lock.Key.ORDER, index = 0, waitTime = 1000, leaseTime = 500, timeUnit = TimeUnit.MILLISECONDS)
	public DeliveryResponse updateToDelivered(Long orderId) {
		Delivery delivery = getDeliveryByOrderId(orderId);
		delivery.updateDelivered();
		return DeliveryResponse.from(delivery);
	}

	// OrderId를 기반으로 Delivery를 조회
	private Delivery getDeliveryByOrderId(Long orderId) {
		// Order 존재 여부 확인
		orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		// Delivery 조회
		return deliveryRepository.findWithOrderByOrderIdOrThrow(orderId, ErrorCode.NOT_FOUND_DELIVERY);
	}
}