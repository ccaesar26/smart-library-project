package ro.unitbv.tpd.ai_service.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class IngestionController {

    private final VectorStore vectorStore;

    public IngestionController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // DTO simplu pentru datele primite
    public record BookIngestDTO(Long id, String title, String summary) {}

    @PostMapping("/ingest")
    public void ingestBook(@RequestBody BookIngestDTO book) {
        // 1. Creăm un Document Spring AI
        // Conținutul principal este rezumatul (pe baza lui se face căutarea)
        // Metadatele ajută la identificarea cărții
        Document doc = new Document(
                book.summary(),
                Map.of("book_id", book.id(), "title", book.title())
        );

        // 2. Salvăm în ChromaDB (Spring AI face embedding automat aici!)
        vectorStore.add(List.of(doc));

        if (vectorStore instanceof SimpleVectorStore simpleStore) {
            simpleStore.save(new File("vector_store.json"));
            System.out.println("💾 VectorStore: Date salvate pe disk.");
        }

        System.out.println("Book indexed in pgvector: " + book.title());
    }
}
