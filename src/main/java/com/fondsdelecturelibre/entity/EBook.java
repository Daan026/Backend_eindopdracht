package com.fondsdelecturelibre.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ebooks")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_content")
    private byte[] fileContent;

    @OneToMany(mappedBy = "ebook", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Review> reviews = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    public EBook(String title, String author, String description, String fileName, 
                String fileType, Long fileSize, User user) {
        setTitle(title);
        setAuthor(author);
        setDescription(description);
        setFileName(fileName);
        setFileType(fileType);
        setFileSize(fileSize);
        setUser(user);
        this.uploadDate = LocalDateTime.now();
    }

    public Set<Review> getReviews() {
        return new HashSet<>(reviews);
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Titel mag niet leeg zijn");
        }
        this.title = title.trim();
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Auteur mag niet leeg zijn");
        }
        this.author = author.trim();
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Beschrijving mag niet null zijn");
        }
        this.description = description.trim();
    }
    
    public void setFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bestandsnaam mag niet leeg zijn");
        }
        this.fileName = fileName.trim();
    }
    
    public void setFileType(String fileType) {
        if (fileType == null || fileType.trim().isEmpty()) {
            throw new IllegalArgumentException("Bestandstype mag niet leeg zijn");
        }
        this.fileType = fileType.trim();
    }

    public void setFileSize(Long fileSize) {
        if (fileSize == null || fileSize <= 0) {
            throw new IllegalArgumentException("Bestandsgrootte moet groter zijn dan 0");
        }
        this.fileSize = fileSize;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = Objects.requireNonNull(uploadDate, "Uploaddatum mag niet null zijn");
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Gebruiker mag niet null zijn");
        }
        this.user = user;
    }
}
