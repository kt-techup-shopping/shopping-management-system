package com.shop.repository.vector;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.vector.Vector;
import com.shop.domain.vector.VectorType;

public interface VectorRepository extends JpaRepository<Vector, Long> {
	Boolean existsByType(VectorType type);

	Optional<Vector> findByType(VectorType type);

	List<Vector> findByTypeIn(List<VectorType> type);
}
