package com.fondsdelecturelibre.service;

import com.fondsdelecturelibre.dto.EBookDTO;
import com.fondsdelecturelibre.entity.EBook;
import com.fondsdelecturelibre.entity.User;
import com.fondsdelecturelibre.exception.ResourceNotFoundException;
import com.fondsdelecturelibre.repository.EBookRepository;
import com.fondsdelecturelibre.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EBookService {

    public byte[] getEBookFileContent(Long id) {
        EBook ebook = ebookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("EBook niet gevonden met id: " + id));
        return ebook.getFileContent();
    }

    private final EBookRepository ebookRepository;
    private final UserRepository userRepository;

    @Autowired
    public EBookService(EBookRepository ebookRepository, UserRepository userRepository) {
        this.ebookRepository = ebookRepository;
        this.userRepository = userRepository;
    }

    public EBookDTO saveEBook(EBookDTO ebookDTO, MultipartFile file) {
        try {
            User user = userRepository.findById(ebookDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User niet gevonden"));

            EBook ebook = new EBook();
            ebook.setTitle(ebookDTO.getTitle());
            ebook.setAuthor(ebookDTO.getAuthor());
            ebook.setDescription(ebookDTO.getDescription());
            ebook.setFileName(file.getOriginalFilename());
            ebook.setFileType(file.getContentType());
            ebook.setFileSize(file.getSize());
            ebook.setUploadDate(java.time.LocalDateTime.now());
            ebook.setFileContent(file.getBytes());
            ebook.setUser(user);

            EBook savedEbook = ebookRepository.save(ebook);
            return convertToDto(savedEbook);
        } catch (IOException e) {
            throw new RuntimeException("Fout bij opslaan van bestand", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<EBookDTO> getAllEBooks(Pageable pageable) {
        Page<EBook> ebookPage = ebookRepository.findAll(pageable);
        return ebookPage.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<EBookDTO> getAllEBooks() {
        return ebookRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EBookDTO> getEBookById(Long id) {
        return ebookRepository.findById(id)
                .map(this::convertToDto);
    }

    public Page<EBookDTO> searchByTitle(String title, Pageable pageable) {
        Page<EBook> ebookPage = ebookRepository.findByTitleContainingIgnoreCase(title, pageable);
        return ebookPage.map(this::convertToDto);
    }

    public List<EBookDTO> searchByTitle(String title) {
        return ebookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteEBook(Long id) {
        EBook ebook = ebookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EBook niet gevonden met id: " + id));
        
        ebookRepository.delete(ebook);
    }

    private EBookDTO convertToDto(EBook ebook) {
        EBookDTO dto = new EBookDTO();
        dto.setId(ebook.getId());
        dto.setTitle(ebook.getTitle());
        dto.setAuthor(ebook.getAuthor());
        dto.setDescription(ebook.getDescription());
        dto.setFileName(ebook.getFileName());
        dto.setFileType(ebook.getFileType());
        dto.setFileSize(ebook.getFileSize());
        dto.setUploadDate(ebook.getUploadDate());
        
        if (ebook.getUser() != null) {
            dto.setUserId(ebook.getUser().getId());
        }
        
        return dto;
    }
}
