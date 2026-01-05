package com.shop.domain.vector;

import com.shop.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vector extends BaseEntity {
	@Column(unique = true)
	@Enumerated(EnumType.STRING)
	private VectorType type;
	private String storedId;
	private String description;
	private String name;
}
