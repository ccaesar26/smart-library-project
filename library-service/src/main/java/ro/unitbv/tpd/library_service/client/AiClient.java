package ro.unitbv.tpd.library_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service") // Numele din Eureka
public interface AiClient {

    record BookIngestDTO(Long id, String title, String summary) {}

    @PostMapping("/api/ai/ingest")
    void ingestBook(@RequestBody BookIngestDTO dto);
}
