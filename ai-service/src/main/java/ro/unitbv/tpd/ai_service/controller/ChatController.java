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
        String localContext = similarDocuments.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n"));

        // Pas 3: Construim Prompt-ul (Mark 8 - Context & Parameterized)
        var systemPrompt = """
            You are the Intelligent Agent for a private library. You have access to Tools (brave_web_search, addNewBook) and a Local Database context.
            
            ### 1. INPUT CONTEXT (Your Library)
            The following text contains the books currently owned by the user.
            [[[ DATABASE_START ]]]
            {localContext}
            [[[ DATABASE_END ]]]
            
            ### 2. EXECUTION PROTOCOL (Check strictly in order)
            
            #### PHASE A: QUERY ANALYSIS
            Check if the user wants to KNOW something or DO something (Add).
            
            #### PHASE B: IF USER ASKS A QUESTION
            1. **Check Local Database**: Look in [[[ DATABASE_START ]]] above.
               - IF FOUND: Answer strictly using that information. Say "Yes, you have this book..."
               - IF NOT FOUND: Use the tool 'brave_web_search' to find the answer on the internet.
            
            #### PHASE C: IF USER WANTS TO ADD A BOOK
            1. **Data Gathering**: The user rarely provides full details.
               - DO NOT ask the user for missing details (like ISBN, year, summary).
               - **MANDATORY**: Call tool 'brave_web_search' to find the correct Author, Publication Year, ISBN, Genre, Page Count, and a comprehensive Summary.
            2. **Execution**:
               - Once you have the real data from the web, Call tool 'addNewBook'.
               - **NEVER** call 'addNewBook' with placeholders like "Unknown" or "0".
               - **ALWAYS** make sure to not forget to call 'addNewBook' and confirm to the user.
            3. **Confirmation**:
               - Tell the user exactly what you added (e.g., "I added 'Dune' by Frank Herbert, published in 1965", NOT "I will add...").
            
            ### EXAMPLES
            - User: "Do I have Ion?" -> Check DATABASE. If yes, say yes. If no, say no.
            - User: "Add Ion by Rebreanu" -> Call 'brave_web_search' for 'Ion Rebreanu details' -> Call 'addNewBook'.
            - User: "What is the Bitcoin price?" -> Call 'brave_web_search'.
            """;

        // Pas 4: Apelăm LLM-ul
        return chatClient.prompt()
                .system(s -> s.text(systemPrompt)
                        .param("role", role)
                        .param("localContext", localContext))
                .user(query)
                .call()
                .content();
    }
}
