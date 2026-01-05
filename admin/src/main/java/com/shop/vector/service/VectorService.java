package com.shop.vector.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.vector.Vector;
import com.shop.domain.vector.VectorType;
import com.shop.openai.VectorApi;
import com.shop.repository.vector.VectorRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VectorService {
	private final VectorRepository vectorRepository;
	private final VectorApi vectorApi;

	@PostConstruct
	void init() {
		if (!vectorRepository.existsByType(VectorType.FAQ)) {
			var name = "FAQ 벡터 스토어";
			var description = "FAQ 벡터 스토어 입니다";
			var vectorStoredId = vectorApi.create(name, description);

			create(
				vectorStoredId,
				name,
				description,
				VectorType.FAQ
			);
		}
	}

	public void create(String storedId, String name, String description, VectorType type) {
		vectorRepository.save(
			new Vector(
				type,
				storedId,
				description,
				name
			)
		);
	}
}
