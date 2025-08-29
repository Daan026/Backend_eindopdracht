package com.fondsdelecturelibre.repository;

import com.fondsdelecturelibre.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEbook_Id(Long ebookId);
    List<Review> findByUser_Username(String username);
    
    // Dashboard queries
    Long countByUser_Username(String username);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.username = :username AND r.id IN (SELECT r2.id FROM Review r2 WHERE r2.id >= :startOfYearId)")
    Long countByUser_UsernameThisYear(@Param("username") String username, @Param("startOfYearId") Long startOfYearId);
    
    List<Review> findTop5ByUser_UsernameOrderByIdDesc(String username);
}
