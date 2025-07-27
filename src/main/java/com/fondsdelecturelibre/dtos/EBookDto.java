package com.fondsdelecturelibre.dtos;

public class EBookDto {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private java.time.LocalDateTime uploadDate;
    private Long userId;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public java.time.LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setUploadDate(java.time.LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EBookDto() {}

    public EBookDto(Long id, String title, String author, String description, String fileName, String fileType, Long fileSize, java.time.LocalDateTime uploadDate, Long userId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.userId = userId;
    }
}
