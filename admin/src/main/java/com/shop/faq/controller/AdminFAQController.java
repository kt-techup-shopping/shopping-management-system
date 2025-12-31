package com.shop.faq.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.faq.request.FAQRequestCreate;
import com.shop.faq.request.FAQRequestSearch;
import com.shop.faq.response.FAQResponseSearch;
import com.shop.faq.service.FAQService;
import com.shop.vector.service.VectorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "FAQ", description = "FAQ API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/faq")
public class AdminFAQController {
	private final FAQService faqService;
	private final VectorService vectorService;

	@Operation(summary = "FAQ 생성", description = "FAQ를 생성합니다.")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> create(
		@RequestBody @Valid FAQRequestCreate request
	) throws Exception {
		faqService.create(request);
		return ApiResult.ok();
	}

	@Operation(summary = "FAQ 삭제", description = "FAQ를 삭제합니다.")
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(
		@PathVariable Long id
	) {
		faqService.delete(id);
		return ApiResult.ok();
	}

	@Operation(summary = "FAQ 검색", description = "Vector Store를 통해 FAQ를 검색합니다.")
	@PostMapping("/search")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<FAQResponseSearch> search(
		@RequestBody @Valid FAQRequestSearch request
	) {
		var response = faqService.search(request);
		return ApiResult.ok(response);
	}
}
