package com.fondsdelecturelibre.service;

import com.fondsdelecturelibre.dto.EBookDTO;
import com.fondsdelecturelibre.entity.EBook;
import com.fondsdelecturelibre.entity.User;
import com.fondsdelecturelibre.repository.EBookRepository;
import com.fondsdelecturelibre.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EBookServiceTest {

    @Mock
    private EBookRepository ebookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EBookService eBookService;

    private User testUser;
    private EBook testEBook;
    private EBookDTO testEBookDTO;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testEBook = new EBook();
        testEBook.setId(1L);
        testEBook.setTitle("Test Book");
        testEBook.setAuthor("Test Author");
        testEBook.setDescription("Test Description");
        testEBook.setFileName("test.pdf");
        testEBook.setFileType("application/pdf");
        testEBook.setFileSize(1024L);
        testEBook.setUploadDate(LocalDateTime.now());
        testEBook.setUser(testUser);

        testEBookDTO = new EBookDTO();
        testEBookDTO.setUserId(1L);
        testEBookDTO.setTitle("Test Book");
        testEBookDTO.setAuthor("Test Author");
        testEBookDTO.setDescription("Test Description");

        testFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
    }

    @Test
    void saveEBook_Success() throws IOException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(ebookRepository.save(any(EBook.class))).thenReturn(testEBook);

        EBookDTO result = eBookService.saveEBook(testEBookDTO, testFile);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        verify(userRepository).findById(1L);
        verify(ebookRepository).save(any(EBook.class));
    }

    @Test
    void saveEBook_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eBookService.saveEBook(testEBookDTO, testFile);
        });

        assertEquals("User niet gevonden", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(ebookRepository, never()).save(any(EBook.class));
    }

    @Test
    void getAllEBooks_Success() {
        List<EBook> ebooks = Arrays.asList(testEBook);
        when(ebookRepository.findAll()).thenReturn(ebooks);

        List<EBookDTO> result = eBookService.getAllEBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(ebookRepository).findAll();
    }

    @Test
    void getEBookById_Success() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(testEBook));

        EBookDTO result = eBookService.getEBookById(1L);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(ebookRepository).findById(1L);
    }

    @Test
    void getEBookById_NotFound() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eBookService.getEBookById(1L);
        });

        assertEquals("EBook niet gevonden", exception.getMessage());
        verify(ebookRepository).findById(1L);
    }

    @Test
    void downloadEBook_Success() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(testEBook));

        byte[] result = eBookService.downloadEBook(1L);

        assertNotNull(result);
        verify(ebookRepository).findById(1L);
    }

    @Test
    void downloadEBook_NotFound() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eBookService.downloadEBook(1L);
        });

        assertEquals("EBook niet gevonden", exception.getMessage());
        verify(ebookRepository).findById(1L);
    }

    @Test
    void updateEBook_Success() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(testEBook));
        when(ebookRepository.save(any(EBook.class))).thenReturn(testEBook);

        testEBookDTO.setTitle("Updated Title");
        EBookDTO result = eBookService.updateEBook(1L, testEBookDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(ebookRepository).findById(1L);
        verify(ebookRepository).save(any(EBook.class));
    }

    @Test
    void updateEBook_NotFound() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eBookService.updateEBook(1L, testEBookDTO);
        });

        assertEquals("EBook niet gevonden", exception.getMessage());
        verify(ebookRepository).findById(1L);
        verify(ebookRepository, never()).save(any(EBook.class));
    }

    @Test
    void deleteEBook_Success() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(testEBook));

        eBookService.deleteEBook(1L);

        verify(ebookRepository).findById(1L);
        verify(ebookRepository).delete(testEBook);
    }

    @Test
    void deleteEBook_NotFound() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eBookService.deleteEBook(1L);
        });

        assertEquals("EBook niet gevonden", exception.getMessage());
        verify(ebookRepository).findById(1L);
        verify(ebookRepository, never()).delete(any(EBook.class));
    }

    @Test
    void getEBooksByUser_Success() {
        List<EBook> ebooks = Arrays.asList(testEBook);
        when(ebookRepository.findByUserId(1L)).thenReturn(ebooks);

        List<EBookDTO> result = eBookService.getEBooksByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(ebookRepository).findByUserId(1L);
    }
}
