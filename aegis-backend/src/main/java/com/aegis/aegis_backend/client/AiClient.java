package com.aegis.aegis_backend.client;

import com.aegis.aegis_backend.dto.AiAnalyzeResponse;
import com.aegis.aegis_backend.dto.AiDecisionRequest;
import com.aegis.aegis_backend.dto.AiDecisionResponse;
import com.aegis.aegis_backend.dto.AiFreshIntelligenceResponse;
import com.aegis.aegis_backend.dto.AiVerifyRequest;
import com.aegis.aegis_backend.dto.AiVerifyResponse;
import com.aegis.aegis_backend.dto.IntelligenceSnapshot;
import com.aegis.aegis_backend.exception.AiServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AiClient {

    private final RestClient aiRestClient;

    public AiClient(RestClient aiRestClient) {
        this.aiRestClient = aiRestClient;
    }

    public AiAnalyzeResponse analyze(
            Long missionId,
            String emergencyType,
            String location,
            String request
    ) {
        try {
            return aiRestClient.post()
                    .uri("/ai/analyze")
                    .body(new AnalyzeRequest(
                            missionId,
                            emergencyType,
                            location,
                            request
                    ))
                    .retrieve()
                    .body(AiAnalyzeResponse.class);

        } catch (Exception e) {
            throw new AiServiceException(
                    "Failed to analyze mission with AI service", e
            );
        }
    }

    public AiFreshIntelligenceResponse requestFreshIntelligence(
            Long missionId
    ) {
        try {
            return aiRestClient.post()
                    .uri("/ai/request-fresh-intelligence")
                    .body(new AiDecisionRequest(missionId))
                    .retrieve()
                    .body(AiFreshIntelligenceResponse.class);

        } catch (Exception e) {
            throw new AiServiceException(
                    "Failed to request fresh intelligence from AI service", e
            );
        }
    }

    public AiVerifyResponse verify(
            Long missionId,
            List<IntelligenceSnapshot> intelligence
    ) {
        try {
            return aiRestClient.post()
                    .uri("/ai/verify")
                    .body(new AiVerifyRequest(
                            missionId,
                            intelligence
                    ))
                    .retrieve()
                    .body(AiVerifyResponse.class);

        } catch (Exception e) {
            throw new AiServiceException(
                    "Failed to verify intelligence with AI service", e
            );
        }
    }

    public AiDecisionResponse decision(Long missionId) {
        try {
            return aiRestClient.post()
                    .uri("/ai/decision")
                    .body(new AiDecisionRequest(missionId))
                    .retrieve()
                    .body(AiDecisionResponse.class);

        } catch (Exception e) {
            throw new AiServiceException(
                    "Failed to get final decision from AI service", e
            );
        }
    }

    private record AnalyzeRequest(
            Long missionId,
            String emergencyType,
            String location,
            String request
    ) {
    }
}