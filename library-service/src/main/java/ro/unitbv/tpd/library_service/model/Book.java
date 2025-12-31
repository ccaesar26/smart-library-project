package ro.unitbv.tpd.library_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Data // Lombok: Getters, Setters, toString, etc.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    private int publicationYear;

    private String publisher;

    private String genre;

    // Text lung pentru rezumat (esențial pentru AI mai târziu)
    @Column(length = 2000)
    private String summary;

    private Integer pageCount;

    private String language;
}
