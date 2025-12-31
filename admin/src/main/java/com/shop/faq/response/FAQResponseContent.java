package com.shop.faq.response;

public record FAQResponseContent(
	String type,
	String text
) {
	public static FAQResponseContent text(String content) {
		return new FAQResponseContent("faq", content);
	}
}

