package com.fondsdelecturelibre.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ebook_id", nullable = false, updatable = false)
    @ToString.Exclude
    private EBook ebook;

    @Builder
    public Review(String content, User user, EBook ebook) {
        setContent(content);
        setUser(user);
        setEbook(ebook);
        this.createdAt = LocalDateTime.now();
    }

    public void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Review inhoud mag niet leeg zijn");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("Review mag maximaal 1000 tekens bevatten");
        }
        this.content = content.trim();
    }
    
    public void setUser(User user) {
        this.user = java.util.Objects.requireNonNull(user, "Gebruiker mag niet null zijn");
    }
    
    public void setEbook(EBook ebook) {
        this.ebook = java.util.Objects.requireNonNull(ebook, "E-book mag niet null zijn");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review review)) return false;
        return id != null && id.equals(review.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
