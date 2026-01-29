package ro.unitbv.tpd.ai_service.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Component
public class LibraryManagerTools {

    private final RestClient restClient;

    public LibraryManagerTools() {
        // Ne conectăm prin Gateway (port 8080)
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    // Record-ul trebuie să coincidă cu structura BookRequest din Library Service
    public record BookToolInput(
            String title,
            String author,
            String isbn,
            int publicationYear,
            String genre,
            String summary,
            int pageCount) {}

    @Tool(description = "Adds a new book to the library system. Use this tool ONLY when the user explicitly asks to add, save, or insert a new book with details.")
    public String addNewBook(BookToolInput book) {
        System.out.println("🤖 AGENT ACTIVAT: Încerc să adaug cartea -> " + book.title());

        try {
            // Generăm header-ul de Admin (pentru că doar Admin are voie să facă POST)
            String authHeader = "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes());

            restClient.post()
                    .uri("/api/books")
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .body(book)
                    .retrieve()
                    .toBodilessEntity();

            return "Success: The book '" + book.title() + "' has been saved to the database.";
        } catch (Exception e) {
            System.err.println("❌ Eroare la tool: " + e.getMessage());
            return "Error: Failed to save the book. Reason: " + e.getMessage();
        }
    }
}