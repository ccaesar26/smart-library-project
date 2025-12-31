package ro.unitbv.tpd.library_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.unitbv.tpd.library_service.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
}
