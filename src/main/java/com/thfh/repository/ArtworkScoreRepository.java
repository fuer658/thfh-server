package com.thfh.repository;

import com.thfh.model.ArtworkScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ArtworkScoreRepository extends JpaRepository<ArtworkScore, Long> {
    @Query("SELECT COUNT(s) > 0 FROM ArtworkScore s WHERE s.artwork.id = :artworkId AND s.user.id = :userId")
    boolean existsByArtworkIdAndUserId(@Param("artworkId") Long artworkId, @Param("userId") Long userId);

    @Query("SELECT AVG(s.score) FROM ArtworkScore s WHERE s.artwork.id = :artworkId")
    Optional<BigDecimal> calculateAverageScore(@Param("artworkId") Long artworkId);

    @Query("SELECT COUNT(s) FROM ArtworkScore s WHERE s.artwork.id = :artworkId")
    long countByArtworkId(@Param("artworkId") Long artworkId);

    @Query("SELECT SUM(s.score) FROM ArtworkScore s WHERE s.artwork.id = :artworkId")
    Optional<BigDecimal> calculateTotalScore(@Param("artworkId") Long artworkId);
}