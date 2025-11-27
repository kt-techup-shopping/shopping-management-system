package com.shop.domain.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.payment.service.PaymentService;
import com.shop.global.common.ApiResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;

	// 결제 완료 처리
	@PutMapping("/internal/{paymentId}/complete")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> completePayment(@PathVariable Long paymentId) {
		paymentService.completePayment(paymentId);

		return ApiResult.ok();
	}

	// 결제 취소 처리
	@PutMapping("{paymentId}/cancel")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> cancelPayment(@PathVariable Long paymentId) {
		paymentService.cancelPayment(paymentId);

		return ApiResult.ok();
	}
}
