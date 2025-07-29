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
    private EBookRepository eBookRepository;

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
        testEBook.setFileSize(1000L);
        testEBook.setFileContent("test content".getBytes());
        testEBook.setUploadDate(LocalDateTime.now());
        testEBook.setUser(testUser);

        testEBookDTO = new EBookDTO();
        testEBookDTO.setTitle("Test Book");
        testEBookDTO.setAuthor("Test Author");
        testEBookDTO.setDescription("Test Description");

        testFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
    }

    @Test
    void saveEBook_ShouldReturnEBookDTO_WhenValidInput() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eBookRepository.save(any(EBook.class))).thenReturn(testEBook);

        // Act
        EBookDTO result = eBookService.saveEBook(testEBookDTO, testFile);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        verify(eBookRepository).save(any(EBook.class));
    }

    @Test
    void saveEBook_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        testEBookDTO.setUserId(999L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            eBookService.saveEBook(testEBookDTO, testFile);
        });
    }

    @Test
    void getAllEBooks_ShouldReturnListOfEBookDTOs() {
        // Arrange
        List<EBook> eBooks = Arrays.asList(testEBook);
        when(eBookRepository.findAll()).thenReturn(eBooks);

        // Act
        List<EBookDTO> result = eBookService.getAllEBooks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
    }

    @Test
    void getEBookById_ShouldReturnEBookDTO_WhenExists() {
        // Arrange
        when(eBookRepository.findById(1L)).thenReturn(Optional.of(testEBook));

        // Act
        Optional<EBookDTO> result = eBookService.getEBookById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
    }

    @Test
    void getEBookById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        when(eBookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<EBookDTO> result = eBookService.getEBookById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getEBookFileContent_ShouldReturnByteArray_WhenExists() {
        // Arrange
        when(eBookRepository.findById(1L)).thenReturn(Optional.of(testEBook));

        // Act
        byte[] result = eBookService.getEBookFileContent(1L);

        // Assert
        assertNotNull(result);
        assertArrayEquals("test content".getBytes(), result);
    }

    @Test
    void getEBookFileContent_ShouldReturnNull_WhenNotExists() {
        // Arrange
        when(eBookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        byte[] result = eBookService.getEBookFileContent(999L);

        // Assert
        assertNull(result);
    }

    @Test
    void deleteEBook_ShouldCallRepository_WhenValidId() {
        // Arrange
        when(eBookRepository.existsById(1L)).thenReturn(true);

        // Act
        eBookService.deleteEBook(1L);

        // Assert
        verify(eBookRepository).deleteById(1L);
    }

    @Test
    void deleteEBook_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(eBookRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            eBookService.deleteEBook(999L);
        });
    }

    @Test
    void searchEBooksByTitle_ShouldReturnFilteredList() {
        // Arrange
        List<EBook> eBooks = Arrays.asList(testEBook);
        when(eBookRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(eBooks);

        // Act
        List<EBookDTO> result = eBookService.searchByTitle("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
    }

    @Test
    void searchEBooksByTitle_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange
        when(eBookRepository.findByTitleContainingIgnoreCase("NonExistent")).thenReturn(Arrays.asList());

        // Act
        List<EBookDTO> result = eBookService.searchByTitle("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
