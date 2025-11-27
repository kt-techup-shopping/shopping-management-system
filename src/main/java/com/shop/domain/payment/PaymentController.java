package com.shop.domain.payment;

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
}
