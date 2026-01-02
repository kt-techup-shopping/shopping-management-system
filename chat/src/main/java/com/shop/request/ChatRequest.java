package com.shop.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatRequest {
	private Long roomId;
	private String message;
}
