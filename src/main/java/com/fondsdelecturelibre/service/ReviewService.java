package com.fondsdelecturelibre.service;

import com.fondsdelecturelibre.dtos.ReviewDto;
import com.fondsdelecturelibre.entity.EBook;
import com.fondsdelecturelibre.entity.Review;
import com.fondsdelecturelibre.entity.User;
import com.fondsdelecturelibre.repository.EBookRepository;
import com.fondsdelecturelibre.repository.ReviewRepository;
import com.fondsdelecturelibre.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EBookRepository eBookRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, EBookRepository eBookRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.eBookRepository = eBookRepository;
    }

    public ReviewDto addReview(ReviewDto reviewDto) {
        User user = userRepository.findById(reviewDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        EBook ebook = eBookRepository.findById(reviewDto.getEbookId())
                .orElseThrow(() -> new RuntimeException("EBook not found"));

        Review review = new Review();
        review.setContent(reviewDto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setUser(user);
        review.setEbook(ebook);

        Review savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    public List<ReviewDto> getReviewsByEbookId(Long ebookId) {
        return reviewRepository.findByEbook_Id(ebookId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRating(5);
        dto.setComment(review.getContent());
        dto.setReviewDate(review.getCreatedAt());
        dto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        dto.setEbookId(review.getEbook() != null ? review.getEbook().getId() : null);
        return dto;
    }
}
