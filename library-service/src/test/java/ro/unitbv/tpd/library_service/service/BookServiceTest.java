package ro.unitbv.tpd.library_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.unitbv.tpd.library_service.dto.BookRequest;
import ro.unitbv.tpd.library_service.model.Book;
import ro.unitbv.tpd.library_service.repository.BookRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

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
    }

    // Test 2: Aruncă excepție la ISBN duplicat
    @Test
    void saveBook_DuplicateIsbn_ThrowsException() {
        when(bookRepository.existsByIsbn(validRequest.getIsbn())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            bookService.saveBook(validRequest);
        });
    }

    // Pentru a ajunge la 10 teste, poți adăuga teste pentru:
    // Test 3: Get All Books returnează listă goală
    // Test 4: Get All Books returnează listă populată
    // Test 5: FindById (trebuie adăugat în service) - Success
    // Test 6: FindById - Not Found
    // Test 7: Delete - Success
    // Test 8: Delete - ID not found
    // Test 9: Update - Success
    // Test 10: Update - Validation fail logic (dacă ai logică complexă)
}
