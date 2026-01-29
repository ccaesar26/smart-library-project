package ro.unitbv.tpd.ai_service.controller;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.tpd.ai_service.tools.LibraryManagerTools;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatController(
            ChatClient.Builder builder,
            VectorStore vectorStore,
            LibraryManagerTools tools,
            List<McpSyncClient> mcpClients
    ) {
        var mcpToolProvider = new SyncMcpToolCallbackProvider(mcpClients);

        this.chatClient = builder
                .defaultTools(tools)
                .defaultToolCallbacks(mcpToolProvider.getToolCallbacks())
                .build();
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
        var systemPrompt = """
            You are a smart, proactive, and organized Library Agent. Your goal is to manage the user's library and answer questions with high accuracy.
            
            ### DECISION PROTOCOL (Follow strictly in order):
            
            1. **ANALYSIS**: Determine if the user wants to ADD a book or ASK a question.
            
            2. **IF ADDING A BOOK**:
               - **Goal**: Call the 'addNewBook' tool with a complete dataset.
               - **Data Enrichment**: If the user provides incomplete details (e.g., only "Add The Hobbit"), you MUST NOT ask the user for more info. Instead:
                 a. Use 'brave_web_search' to find the exact Author, original Publication Year, a real ISBN, and the correct Genre.
                 b. Generate a comprehensive, engaging Summary (2-3 sentences) in the user's language.
               - **Execution**: Once you have all fields (Title, Author, ISBN, Year, Genre, Summary, PageCount), call 'addNewBook'.
               - **Response**: Confirm the action to the user, mentioning the specific details you found/added.
            
            3. **IF ANSWERING A QUESTION**:
               - **Step A (Local Context)**: Check the [LIBRARY CONTEXT] below first. If the answer is there, use it. This is the source of truth for what the user *owns*.
               - **Step B (External Search)**: If the answer is NOT in the local context (or asks for external info like "reviews", "awards", "other books by author"), use 'brave_web_search'.
               - **Language**: Always answer in the same language as the user's query.
            
            ### LIBRARY CONTEXT (RAG Data):
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
