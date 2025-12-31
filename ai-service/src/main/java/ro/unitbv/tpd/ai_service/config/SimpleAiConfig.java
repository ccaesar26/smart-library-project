package ro.unitbv.tpd.ai_service.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class SimpleAiConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // Aceasta salvează vectorii într-un fișier local. E simplu, rapid și nu crapă.
        var simpleVectorStore =  SimpleVectorStore.builder(embeddingModel).build();

        // Încărcăm datele vechi dacă există fișierul
        File vectorFile = new File("vector_store.json");
        if (vectorFile.exists()) {
            simpleVectorStore.load(vectorFile);
            System.out.println("✅ VectorStore: Date încărcate din fișier.");
        }

        return simpleVectorStore;
    }
}