package com.fondsdelecturelibre.repository;

import com.fondsdelecturelibre.entity.EBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EBookRepository extends JpaRepository<EBook, Long> {
    List<EBook> findByUserUsername(String username);
    List<EBook> findByTitleContainingIgnoreCase(String title);
    Page<EBook> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Optional<EBook> findByFileName(String fileName);
}
