package com.fondsdelecturelibre.controller;

import com.fondsdelecturelibre.dtos.ReviewDto;
import com.fondsdelecturelibre.service.ReviewService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ebooks")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{ebookId}/reviews")
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable Long ebookId,
            @RequestBody ReviewDto reviewDto) {
        reviewDto.setEbookId(ebookId);
        ReviewDto createdReview = reviewService.addReview(reviewDto);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/{ebookId}/reviews")
    public ResponseEntity<List<ReviewDto>> getReviewsByEbook(@PathVariable Long ebookId) {
        return ResponseEntity.ok(reviewService.getReviewsByEbookId(ebookId));
    }
}
