package com.shop.global.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다."),
	NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
	EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
	FAIL_LOGIN(HttpStatus.BAD_REQUEST, "아이디 혹은 비밀번호가 일치하지 않습니다."),
	DOES_NOT_MATCH_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다."),
	CAN_NOT_ALLOWED_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),
	NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "필수값 누락입니다."),
	NOT_ACTIVE(HttpStatus.BAD_REQUEST, "상품이 구매가능한 상태가 아닙니다."),
	MIN_PIECE(HttpStatus.BAD_REQUEST, "1개 이상이어야 합니다."),
	FAIL_ACQUIRED_LOCK(HttpStatus.BAD_REQUEST, "락 획득에 실패했습니다."),
	ERROR_SYSTEM(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 오류가 발생했습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "다시 로그인 해주세요."),

	// Review
	NOT_PURCHASED_PRODUCT(HttpStatus.BAD_REQUEST, "구매한 적 없는 상품입니다."),
	NOT_FOUND_REVIEW(HttpStatus.BAD_REQUEST, "존재하지 않는 리뷰입니다."),
	DOES_NOT_MATCH_USER_REVIEW(HttpStatus.BAD_REQUEST, "리뷰는 작성자만 삭제할 수 있습니다."),
	ALREADY_WRITE_REVIEW(HttpStatus.BAD_REQUEST, "이미 상품에 대한 리뷰를 작성했습니다."),

	;

	private final HttpStatus status;
	private final String message;

}
