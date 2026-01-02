package com.shop.repository.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shop.domain.chat.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	// 최초 요청 (가장 최신 메시지)
	List<Chat> findTop20ByRoomIdOrderByCreatedAtDesc(Long roomId);

	@Query("""
    SELECT c FROM Chat c
    WHERE c.roomId = :roomId
      AND (c.createdAt < :createdAt)
    ORDER BY c.createdAt DESC, c.id DESC
""")
	List<Chat> findChatsByCursor(
		@Param("roomId") Long roomId,
		@Param("createdAt") LocalDateTime createdAt,
		@Param("id") UUID id,
		Pageable pageable
	);

}
