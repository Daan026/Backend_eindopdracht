package com.fondsdelecturelibre.service;

import com.fondsdelecturelibre.dto.DashboardDTO;
import com.fondsdelecturelibre.dto.EBookDTO;
import com.fondsdelecturelibre.dto.ReviewDTO;
import com.fondsdelecturelibre.entity.EBook;
import com.fondsdelecturelibre.entity.Review;
import com.fondsdelecturelibre.repository.EBookRepository;
import com.fondsdelecturelibre.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final EBookRepository ebookRepository;
    private final ReviewRepository reviewRepository;
    
    @Transactional(readOnly = true)
    public DashboardDTO getUserDashboard(String username) {
        // Get user statistics
        DashboardDTO.UserStatisticsDTO statistics = getUserStatistics(username);
        
        // Get recent uploads (last 5)
        List<EBookDTO> recentUploads = getRecentUploads(username);
        
        // Get recent reviews (last 5)
        List<ReviewDTO> recentReviews = getRecentReviews(username);
        
        return new DashboardDTO(statistics, recentUploads, recentReviews);
    }
    
    private DashboardDTO.UserStatisticsDTO getUserStatistics(String username) {
        Long totalBooksUploaded = ebookRepository.countByUserUsername(username);
        Long totalReviewsWritten = reviewRepository.countByUser_Username(username);
        
        // Calculate start of current year
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Long booksUploadedThisYear = ebookRepository.countByUserUsernameAndUploadDateAfter(username, startOfYear);
        
        // For reviews this year, we'll use a simple approximation since we don't have timestamps
        Long reviewsThisYear = totalReviewsWritten; // Simplified for now
        
        return new DashboardDTO.UserStatisticsDTO(
            totalBooksUploaded,
            totalReviewsWritten,
            booksUploadedThisYear,
            reviewsThisYear,
            null, // memberSince not available in current schema
            "Unknown" // mostActiveCategory - would need complex query
        );
    }
    
    private List<EBookDTO> getRecentUploads(String username) {
        List<EBook> recentBooks = ebookRepository.findTop5ByUserUsernameOrderByUploadDateDesc(username);
        return recentBooks.stream()
                .map(this::convertToEBookDTO)
                .collect(Collectors.toList());
    }
    
    private List<ReviewDTO> getRecentReviews(String username) {
        List<Review> recentReviews = reviewRepository.findTop5ByUser_UsernameOrderByIdDesc(username);
        return recentReviews.stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());
    }
    
    private EBookDTO convertToEBookDTO(EBook ebook) {
        EBookDTO dto = new EBookDTO();
        dto.setId(ebook.getId());
        dto.setTitle(ebook.getTitle());
        dto.setAuthor(ebook.getAuthor());
        dto.setDescription(ebook.getDescription());
        dto.setFileName(ebook.getFileName());
        dto.setFileType(ebook.getFileType());
        dto.setFileSize(ebook.getFileSize());
        dto.setUploadDate(ebook.getUploadDate());
        dto.setUserId(ebook.getUser().getId());
        return dto;
    }
    
    private ReviewDTO convertToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setEbookId(review.getEbook().getId());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        return dto;
    }
}
