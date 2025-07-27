package com.fondsdelecturelibre.repository;

import com.fondsdelecturelibre.entity.EBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EBookRepository extends JpaRepository<EBook, Long> {
    List<EBook> findByUserUsername(String username);
    List<EBook> findByTitleContainingIgnoreCase(String title);
}
