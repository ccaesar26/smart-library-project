package ro.unitbv.tpd.library_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookRequest {

    // 1. Titlu obligatoriu
    @NotBlank(message = "Title is required")
    // 2. Mărime titlu
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 chars")
    private String title;

    // 3. Autor obligatoriu
    @NotBlank(message = "Author is required")
    private String author;

    // 4. Format ISBN (Regex simplificat)
    @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$", message = "Invalid ISBN format")
    private String isbn;

    // 5. Anul publicării (nu în viitor - suntem în 2025)
    @Max(value = 2025, message = "Year cannot be in the future")
    // 6. Anul să nu fie prea vechi
    @Min(value = 1450, message = "Year must be after the printing press invention")
    private int publicationYear;

    // 7. Rezumat obligatoriu (pt AI)
    @NotBlank(message = "Summary is required for AI processing")
    // 8. Lungime rezumat
    @Size(min = 20, max = 2000, message = "Summary must be between 20 and 2000 chars")
    private String summary;

    // 9. Număr pagini pozitiv
    @Positive(message = "Page count must be positive")
    private Integer pageCount;

    // 10. Genul cărții
    @NotBlank(message = "Genre is required")
    private String genre;
}
