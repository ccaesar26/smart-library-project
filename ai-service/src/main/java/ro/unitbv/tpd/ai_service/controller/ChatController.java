package ro.unitbv.tpd.ai_service.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String query,
                       @RequestParam(defaultValue = "helpful librarian") String role) {

        // Pas 1: Căutăm în ChromaDB bucăți relevante (Top 3 rezultate)
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(3).build()
//                SearchRequest.query(query).withTopK(3)
        );

        // Pas 2: Construim contextul din rezultatele găsite
        String information = similarDocuments.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n"));

        // Pas 3: Construim Prompt-ul (Mark 8 - Context & Parameterized)
        String systemPrompt = """
                You are a {role}.
                Use the following information about the user's library to answer the question.
                If the answer is not in the information, say "I don't have that book in my records."
                
                INFORMATION:
                {information}
                """;

        // Pas 4: Apelăm LLM-ul
        return chatClient.prompt()
                .system(s -> s.text(systemPrompt)
                        .param("role", role)
                        .param("information", information))
                .user(query)
                .call()
                .content();
    }
}
