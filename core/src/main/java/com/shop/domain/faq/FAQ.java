package com.shop.domain.faq;

import com.shop.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FAQ extends BaseEntity {
	private String title;
	private String content;
	@Enumerated(EnumType.STRING)
	private Category category;
	private String fileId;
	private final String createdBy = "ADMIN";

	public FAQ(String title, String content, Category category) {
		this.title = title;
		this.content = content;
		this.category = category;
	}

	public void updateFileId(String fileId) {
		this.fileId = fileId;
	}
}