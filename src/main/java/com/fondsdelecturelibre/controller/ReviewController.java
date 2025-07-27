package com.fondsdelecturelibre.controller;

import com.fondsdelecturelibre.dtos.ReviewDto;
import com.fondsdelecturelibre.service.ReviewService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ebooks/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
        ReviewDto createdReview = reviewService.addReview(reviewDto);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/ebook/{ebookId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByEbook(@PathVariable Long ebookId) {
        return ResponseEntity.ok(reviewService.getReviewsByEbookId(ebookId));
    }
}
