package com.fondsdelecturelibre.repository;

import com.fondsdelecturelibre.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEbook_Id(Long ebookId);
    List<Review> findByUser_Username(String username);
}
