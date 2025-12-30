package com.shop.domain.vector;

import java.util.List;

public enum VectorType {
	FAQ,
	NOTICE,
	COMMUNITY;

	public static List<VectorType> chatbotRange() {
		return List.of(NOTICE, FAQ);
	}
}
