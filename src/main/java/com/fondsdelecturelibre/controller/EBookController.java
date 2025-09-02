package com.fondsdelecturelibre.controller;

import com.fondsdelecturelibre.dto.EBookDTO;
import com.fondsdelecturelibre.exception.ResourceNotFoundException;
import com.fondsdelecturelibre.service.EBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ebooks")
public class EBookController {

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadEBook(@PathVariable Long id) {
        try {
            EBookDTO ebook = ebookService.getEBookById(id).orElseThrow(() -> new ResourceNotFoundException("EBook niet gevonden met id: " + id));
            byte[] content = ebookService.getEBookFileContent(id); 
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ebook.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private final EBookService ebookService;

    @Autowired
    public EBookController(EBookService ebookService) {
        this.ebookService = ebookService;
    }

    @PostMapping
    public ResponseEntity<EBookDTO> uploadEBook(
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam String description) throws IOException {
        
        // Automatisch huidige gebruiker detecteren via SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        EBookDTO ebookDTO = new EBookDTO();
        ebookDTO.setTitle(title);
        ebookDTO.setAuthor(author);
        ebookDTO.setDescription(description);
        ebookDTO.setFileType(file.getContentType());
        ebookDTO.setFileSize(file.getSize());
        ebookDTO.setFileName(file.getOriginalFilename());

        EBookDTO savedEBook = ebookService.saveEBook(ebookDTO, file, username);
        return new ResponseEntity<>(savedEBook, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<EBookDTO>> getAllEBooks(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<EBookDTO> ebooks = ebookService.getAllEBooks(pageable);
        return new ResponseEntity<>(ebooks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EBookDTO> getEBookById(@PathVariable Long id) {
        EBookDTO ebookDTO = ebookService.getEBookById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EBook niet gevonden met id: " + id));
        return new ResponseEntity<>(ebookDTO, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EBookDTO>> searchEBooks(
            @RequestParam String title,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<EBookDTO> ebooks = ebookService.searchByTitle(title, pageable);
        return new ResponseEntity<>(ebooks, HttpStatus.OK);
    }
    
    @GetMapping("/search/author")
    public ResponseEntity<Page<EBookDTO>> searchEBooksByAuthor(
            @RequestParam String author,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<EBookDTO> ebooks = ebookService.searchByAuthor(author, pageable);
        return new ResponseEntity<>(ebooks, HttpStatus.OK);
    }
    
    @GetMapping("/search/category")
    public ResponseEntity<Page<EBookDTO>> searchEBooksByCategory(
            @RequestParam Long categoryId,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<EBookDTO> ebooks = ebookService.searchByCategory(categoryId, pageable);
        return new ResponseEntity<>(ebooks, HttpStatus.OK);
    }
    
    @GetMapping("/search/advanced")
    public ResponseEntity<Page<EBookDTO>> advancedSearchEBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<EBookDTO> ebooks = ebookService.advancedSearch(title, author, categoryId, pageable);
        return new ResponseEntity<>(ebooks, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EBookDTO> updateEBook(
            @PathVariable Long id,
            @RequestBody EBookDTO ebookDTO) {
        EBookDTO updatedEBook = ebookService.updateEBook(id, ebookDTO);
        return new ResponseEntity<>(updatedEBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEBook(@PathVariable Long id) {
        ebookService.deleteEBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
