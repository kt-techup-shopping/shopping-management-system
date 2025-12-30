package com.shop.repository.faq;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.faq.FAQ;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
}
