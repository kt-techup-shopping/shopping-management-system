package com.shop;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다."),
	NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
	NOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리를 찾을 수 없습니다."),
	NOT_FOUND_ORDER(HttpStatus.BAD_REQUEST, "주문을 찾을 수 없습니다."),
	NOT_FOUND_DELIVERY(HttpStatus.BAD_REQUEST, "배송 정보를 찾을 수 없습니다."),
	EXIST_LOGINID(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
	EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
	FAIL_LOGIN(HttpStatus.BAD_REQUEST, "아이디 혹은 비밀번호가 일치하지 않습니다."),
	DOES_NOT_MATCH_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다."),
	CAN_NOT_ALLOWED_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),
	ACCOUNT_INACTIVATED(HttpStatus.BAD_REQUEST, "비활성화 된 계정입니다."),
	NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "필수값 누락입니다."),
	INVALID_SORT_OPTION(HttpStatus.BAD_REQUEST, "잘못된 정렬 옵션입니다."),
	NOT_ACTIVE(HttpStatus.BAD_REQUEST, "상품이 구매가능한 상태가 아닙니다."),
	MIN_PIECE(HttpStatus.BAD_REQUEST, "1개 이상이어야 합니다."),
	FAIL_ACQUIRED_LOCK(HttpStatus.BAD_REQUEST, "락 획득에 실패했습니다."),
	ERROR_SYSTEM(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 오류가 발생했습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "다시 로그인 해주세요."),
	PAGE_INVALID(HttpStatus.BAD_REQUEST, "Page 값은 1 이상이어야 합니다."),
	SIZE_INVALID(HttpStatus.BAD_REQUEST, "Size 값은 1 이상이어야 합니다."),
	NOT_USER_ROLE_ADMIN(HttpStatus.BAD_REQUEST, "해당 유저는 관리자 권한이 아닙니다."),
	WAYBILL_NO_REQUIRED(HttpStatus.BAD_REQUEST, "송장번호는 필수입니다."),

	// Product
	INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 상태입니다."),
	INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "재고 수량은 음수일 수 없습니다."),
	// Review
	NOT_PURCHASED_PRODUCT(HttpStatus.BAD_REQUEST, "구매한 적 없는 상품입니다."),
	NOT_FOUND_REVIEW(HttpStatus.BAD_REQUEST, "존재하지 않는 리뷰입니다."),
	DOES_NOT_MATCH_USER_REVIEW(HttpStatus.BAD_REQUEST, "리뷰는 작성자만 삭제할 수 있습니다."),
	ALREADY_WRITE_REVIEW(HttpStatus.BAD_REQUEST, "이미 상품에 대한 리뷰를 작성했습니다."),
	NOT_FOUND_ADMIN_REVIEW(HttpStatus.BAD_REQUEST, "존재하지 않는 관리자 리뷰입니다."),
	ALREADY_WRITE_ADMIN_REVIEW(HttpStatus.BAD_REQUEST, "이미 해당 리뷰에 대한 관리자 리뷰를 작성했습니다."),

	// Order
	INVALID_ORDER_OWNER(HttpStatus.BAD_REQUEST, "본인 주문만 조회할 수 있습니다."),
	//	INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태입니다."),
	INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "주문을 처리할 수 없습니다."),
	ALREADY_PENDING_ORDER(HttpStatus.BAD_REQUEST, "이미 결제 대기중인 주문입니다."),
	ALREADY_PAID_ORDER(HttpStatus.BAD_REQUEST, "이미 주문에 대한 결제가 완료되었습니다."),
	INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, "주문 정보가 일치하지 않습니다"),
	ORDER_PAYMENT_MISMATCH(HttpStatus.BAD_REQUEST, "주문과 결제 정보가 일치하지 않습니다."),

	// Payment
	NOT_FOUND_PAYMENT(HttpStatus.BAD_REQUEST, "해당 결제 내역이 존재하지 않습니다."),
	INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "결제를 처리할 수 없습니다."),
	REQUIRED_ORDER_FOR_PAYMENT(HttpStatus.BAD_REQUEST, "주문 정보가 필요합니다."),
	INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
	;

	private final HttpStatus status;
	private final String message;

}
