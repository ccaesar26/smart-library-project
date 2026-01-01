package ro.unitbv.tpd.library_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.unitbv.tpd.library_service.client.AiClient;
import ro.unitbv.tpd.library_service.dto.BookRequest;
import ro.unitbv.tpd.library_service.model.Book;
import ro.unitbv.tpd.library_service.repository.BookRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AiClient aiClient;

    @InjectMocks
    private BookService bookService;

    private BookRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new BookRequest();
        validRequest.setTitle("Spring in Action");
        validRequest.setAuthor("Craig Walls");
        validRequest.setIsbn("9781617294945");
        validRequest.setPublicationYear(2022);
        validRequest.setSummary("Great book about Spring Boot");
        validRequest.setPageCount(500);
        validRequest.setGenre("Tech");
    }

    // Test 1: Salvare cu succes
    @Test
    void saveBook_Success() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);

        Book saved = bookService.saveBook(validRequest);

        assertNotNull(saved);
        assertEquals("Spring in Action", saved.getTitle());
        verify(aiClient, times(1)).ingestBook(any(AiClient.BookIngestDTO.class));
    }

    // Test 2: Aruncă excepție la ISBN duplicat
    @Test
    void saveBook_DuplicateIsbn_ThrowsException() {
        when(bookRepository.existsByIsbn(validRequest.getIsbn())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            bookService.saveBook(validRequest);
        });
    }

    // Test 3: Get All Books returnează listă goală
    @Test
    void getAllBooks_EmptyList() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<Book> books = bookService.getAllBooks();

        assertTrue(books.isEmpty());
    }

    // Test 4: Get All Books returnează listă populată
    @Test
    void getAllBooks_PopulatedList() {
        Book book = new Book();
        book.setTitle("Test Book");
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> books = bookService.getAllBooks();

        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
    }

    // Test 5: FindById - Success
    @Test
    void getBookById_Success() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book found = bookService.getBookById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    // Test 6: FindById - Not Found
    @Test
    void getBookById_NotFound_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookService.getBookById(1L));
    }

    // Test 7: Delete - Success
    @Test
    void deleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));

        verify(bookRepository, times(1)).deleteById(1L);
    }

    // Test 8: Delete - ID not found
    @Test
    void deleteBook_NotFound_ThrowsException() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bookService.deleteBook(1L));
    }

    // Test 9: Update - Success
    @Test
    void updateBook_Success() {
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);

        BookRequest updateRequest = new BookRequest();
        updateRequest.setTitle("New Title");
        updateRequest.setAuthor("New Author");
        updateRequest.setIsbn("1234567890");
        updateRequest.setPublicationYear(2023);
        updateRequest.setSummary("New Summary");
        updateRequest.setPageCount(200);
        updateRequest.setGenre("Fiction");

        Book updated = bookService.updateBook(1L, updateRequest);

        assertEquals("New Title", updated.getTitle());
        assertEquals("New Author", updated.getAuthor());
    }

    // Test 10: Update - Not Found (Validation fail logic)
    @Test
    void updateBook_NotFound_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(1L, new BookRequest()));
    }
}
