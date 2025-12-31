package ro.unitbv.tpd.library_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.unitbv.tpd.library_service.dto.BookRequest;
import ro.unitbv.tpd.library_service.model.Book;
import ro.unitbv.tpd.library_service.repository.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor // Injectează automat constructorul pentru final fields
public class BookService {

    private final BookRepository bookRepository;

    public Book saveBook(BookRequest request) {
        // Verificare Business Logic (bună pentru teste unitare)
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("Book with this ISBN already exists");
        }

        // Mapare manuală DTO -> Entity (sau poți folosi ModelMapper)
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .publicationYear(request.getPublicationYear())
                .summary(request.getSummary())
                .pageCount(request.getPageCount())
                .genre(request.getGenre())
                .build();

        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
