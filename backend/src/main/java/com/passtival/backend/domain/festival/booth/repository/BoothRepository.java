package com.passtival.backend.domain.festival.booth.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;


public interface BoothRepository extends JpaRepository<Booth, Long> {

	@Query("""
SELECT b FROM Booth b
WHERE (:lastType IS NULL
   OR CASE b.type
        WHEN '학내부스' THEN 1
        WHEN '체험'     THEN 2
        WHEN '푸드존'   THEN 3
        WHEN '의료지원' THEN 4
        ELSE 5
      END > :lastTypeOrder
   OR (CASE b.type
        WHEN '학내부스' THEN 1
        WHEN '체험'     THEN 2
        WHEN '푸드존'   THEN 3
        WHEN '의료지원' THEN 4
        ELSE 5
      END = :lastTypeOrder AND b.name > :lastName)
   OR (CASE b.type
        WHEN '학내부스' THEN 1
        WHEN '체험'     THEN 2
        WHEN '푸드존'   THEN 3
        WHEN '의료지원' THEN 4
        ELSE 5
      END = :lastTypeOrder AND b.name = :lastName AND b.id < :lastId)
)
ORDER BY
  CASE b.type
    WHEN '학내부스' THEN 1
    WHEN '체험'     THEN 2
    WHEN '푸드존'   THEN 3
    WHEN '의료지원' THEN 4
    ELSE 5
  END,
  b.name ASC,
  b.id DESC
""")
	List<Booth> findPageByCursor(
		@Param("lastTypeOrder") Integer lastTypeOrder,
		@Param("lastType") String lastType,
		@Param("lastName") String lastName,
		@Param("lastId") Long lastId,
		Pageable pageable
	);


	boolean existsByName(String name);

}
