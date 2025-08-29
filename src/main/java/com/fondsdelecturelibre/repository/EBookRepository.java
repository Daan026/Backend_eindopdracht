package com.fondsdelecturelibre.repository;

import com.fondsdelecturelibre.entity.EBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EBookRepository extends JpaRepository<EBook, Long> {
    List<EBook> findByUserUsername(String username);
    List<EBook> findByTitleContainingIgnoreCase(String title);
    Page<EBook> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Optional<EBook> findByFileName(String fileName);
    
    // Dashboard queries
    Long countByUserUsername(String username);
    
    @Query("SELECT COUNT(e) FROM EBook e WHERE e.user.username = :username AND e.uploadDate >= :startOfYear")
    Long countByUserUsernameAndUploadDateAfter(@Param("username") String username, @Param("startOfYear") LocalDateTime startOfYear);
    
    List<EBook> findTop5ByUserUsernameOrderByUploadDateDesc(String username);
    
    // Advanced Search queries
    List<EBook> findByAuthorContainingIgnoreCase(String author);
    Page<EBook> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    @Query("SELECT e FROM EBook e WHERE e.category.id = :categoryId")
    List<EBook> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT e FROM EBook e WHERE e.category.id = :categoryId")
    Page<EBook> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT e FROM EBook e WHERE " +
           "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(e.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:categoryId IS NULL OR e.category.id = :categoryId)")
    Page<EBook> findByAdvancedSearch(@Param("title") String title, 
                                    @Param("author") String author, 
                                    @Param("categoryId") Long categoryId, 
                                    Pageable pageable);
}
