package com.shop.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
	// 1. 네이티브쿼리로 작성
	// 2. jqpl로 작성
	// 3. 쿼리메소드로 어찌저찌 작성
	// 4. 조회할때는 동적쿼리를 작성하게해줄 수 있는 querydsl 사용하자
}
