package com.shop.payment.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.payment.request.PaymentConfirmRequest;
import com.shop.payment.response.PaymentInfoResponse;
import com.shop.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "결제", description = "결제를 관리하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
	private final PaymentService paymentService;

	// 결제 정보 요청
	@GetMapping("/{paymentId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<PaymentInfoResponse> getPaymentPageInfo(@PathVariable Long paymentId) {
		var paymentInfo = paymentService.getPaymentInfo(paymentId);
		return ApiResult.ok(paymentInfo);
	}

	// 결제 성공 페이지로 라디아렉트용
	@GetMapping("/toss/success")
	public String tossSuccess(
		@RequestParam Long paymentId,
		@RequestParam String orderId,
		@RequestParam String paymentKey,
		@RequestParam Long amount
	) {
		// 정적 성공 페이지로 이동
		return "redirect:/payment-success.html"
			+ "?paymentId=" + paymentId
			+ "&orderId=" + URLEncoder.encode(orderId, StandardCharsets.UTF_8)
			+ "&paymentKey=" + URLEncoder.encode(paymentKey, StandardCharsets.UTF_8)
			+ "&amount=" + amount;
	};

	@Operation(summary = "결제 확인", description = "결제 ID를 통해 결제를 확정합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.INVALID_PAYMENT_STATUS,
		ErrorCode.INVALID_ORDER_ID,
		ErrorCode.INVALID_PAYMENT_AMOUNT,
	})
	@PostMapping("/{paymentId}/confirm")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> tossConfirm(
		@PathVariable Long paymentId,
		@RequestBody PaymentConfirmRequest request
	) {
		paymentService.confirm(
			paymentId,
			request.orderId(),
			request.paymentKey(),
			request.amount()
		);

		return ApiResult.ok();
	}

	// 결제 완료 처리
	@Operation(summary = "결제 완료", description = "결제 완료 처리를 진행합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_PAYMENT,
		ErrorCode.INVALID_PAYMENT_STATUS,
		ErrorCode.INVALID_ORDER_STATUS,
	})
	@PutMapping("/internal/{paymentId}/complete")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> completePayment(@PathVariable Long paymentId) {
		paymentService.completePayment(paymentId);

		return ApiResult.ok();
	}

	// 결제 취소 처리
	@Operation(summary = "결제 취소", description = "결제 취소 처리를 진행합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_PAYMENT,
		ErrorCode.INVALID_PAYMENT_STATUS,
		ErrorCode.INVALID_ORDER_STATUS,
	})
	@PutMapping("{paymentId}/cancel")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> cancelPayment(@PathVariable Long paymentId) {
		paymentService.cancelPayment(paymentId);

		return ApiResult.ok();
	}
}
