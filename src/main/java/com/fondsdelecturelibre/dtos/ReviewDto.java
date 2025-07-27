package com.fondsdelecturelibre.dtos;

public class ReviewDto {
    private Long id;
    private int rating;
    private String comment;
    private java.time.LocalDateTime reviewDate;
    private Long userId;
    private Long ebookId;

    public Long getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public java.time.LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEbookId() {
        return ebookId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setReviewDate(java.time.LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEbookId(Long ebookId) {
        this.ebookId = ebookId;
    }

    public ReviewDto() {}

    public ReviewDto(Long id, int rating, String comment, java.time.LocalDateTime reviewDate, Long userId, Long ebookId) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.userId = userId;
        this.ebookId = ebookId;
    }
}
