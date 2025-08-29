package com.fondsdelecturelibre.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private UserStatisticsDTO userStatistics;
    private List<EBookDTO> recentUploads;
    private List<ReviewDTO> recentReviews;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatisticsDTO {
        private Long totalBooksUploaded;
        private Long totalReviewsWritten;
        private Long booksUploadedThisYear;
        private Long reviewsThisYear;
        private LocalDateTime memberSince;
        private String mostActiveCategory;
    }
}
