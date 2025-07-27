package com.fondsdelecturelibre.controller;

import com.fondsdelecturelibre.dto.EBookDTO;
import com.fondsdelecturelibre.exception.ResourceNotFoundException;
import com.fondsdelecturelibre.service.EBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        
        EBookDTO ebookDTO = new EBookDTO();
        ebookDTO.setTitle(title);
        ebookDTO.setAuthor(author);
        ebookDTO.setDescription(description);
        ebookDTO.setFileType(file.getContentType());
        ebookDTO.setFileSize(file.getSize());
        ebookDTO.setFileName(file.getOriginalFilename());

        EBookDTO savedEBook = ebookService.saveEBook(ebookDTO, file);
        return new ResponseEntity<>(savedEBook, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EBookDTO>> getAllEBooks() {
        List<EBookDTO> ebooks = ebookService.getAllEBooks();
        return new ResponseEntity<>(ebooks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EBookDTO> getEBookById(@PathVariable Long id) {
        EBookDTO ebookDTO = ebookService.getEBookById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EBook niet gevonden met id: " + id));
        return new ResponseEntity<>(ebookDTO, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EBookDTO>> searchEBooks(@RequestParam String title) {
        List<EBookDTO> ebooks = ebookService.searchByTitle(title);
        return new ResponseEntity<>(ebooks, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEBook(@PathVariable Long id) {
        ebookService.deleteEBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
