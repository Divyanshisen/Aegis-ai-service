package com.aegis.aegis_backend.service;

import com.aegis.aegis_backend.client.AiClient;
import com.aegis.aegis_backend.dto.*;
import com.aegis.aegis_backend.entity.DecisionEvent;
import com.aegis.aegis_backend.entity.Intelligence;
import com.aegis.aegis_backend.entity.Mission;
import com.aegis.aegis_backend.repository.DecisionEventRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiIntegrationService {

    private final AiClient aiClient;
    private final MissionService missionService;
    private final IntelligenceService intelligenceService;
    private final DecisionEventRepository decisionEventRepository;
    private final ObjectMapper objectMapper;

    public AiIntegrationService(
            AiClient aiClient,
            MissionService missionService,
            IntelligenceService intelligenceService,
            DecisionEventRepository decisionEventRepository,
            ObjectMapper objectMapper) {

        this.aiClient = aiClient;
        this.missionService = missionService;
        this.intelligenceService = intelligenceService;
        this.decisionEventRepository = decisionEventRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Phase 3:
     * Ask the AI service what intelligence is required for the mission.
     */
    @Transactional
    public AiAnalyzeResponse determineRequiredIntelligence(Long missionId) {

        Mission mission = missionService.getMissionById(missionId);

        AiAnalyzeResponse response = aiClient.analyze(
                mission.getId(),
                mission.getEmergencyType().name(),
                mission.getLocation(),
                mission.getRequest()
        );

        logDecision(
                mission,
                "AI_ANALYSIS",
                response.getReason(),
                response
        );

        Mission.MissionStatus nextStatus;

        if (Boolean.TRUE.equals(response.getNeedsFreshData())) {
            nextStatus = Mission.MissionStatus.AWAITING_PAYMENT;
        } else {
            nextStatus = Mission.MissionStatus.GATHERING_INTELLIGENCE;
        }

        missionService.updateStatus(missionId, nextStatus);

        return response;
    }

    /**
     * Phase 3:
     * Send gathered intelligence to the AI service for verification.
     */
    @Transactional
    public AiVerifyResponse verifyGatheredIntelligence(Long missionId) {

        Mission mission = missionService.getMissionById(missionId);

        List<Intelligence> gathered =
                intelligenceService.getIntelligenceForMission(missionId);

        List<IntelligenceSnapshot> snapshots = gathered.stream()
                .map(intel -> new IntelligenceSnapshot(
                        intel.getType(),
                        intel.getSource(),
                        intel.getData(),
                        intel.getConfidence(),
                        intel.getTimestamp()
                ))
                .toList();

        AiVerifyResponse response =
                aiClient.verify(missionId, snapshots);

        logDecision(
                mission,
                "INTELLIGENCE_VERIFICATION",
                response.getRecommendation(),
                response
        );

        if (response.isVerified()) {
            missionService.updateStatus(
                    missionId,
                    Mission.MissionStatus.VERIFIED
            );
        } else {
            missionService.updateStatus(
                    missionId,
                    Mission.MissionStatus.AWAITING_PAYMENT
            );
        }

        return response;
    }

    /**
     * Phase 4:
     * Tell the AI service that fresh intelligence should be requested.
     *
     * The AI service returns PENDING_PAYMENT.
     * Actual x402 payment is handled by the Spring Boot payment flow.
     */
    @Transactional
    public AiFreshIntelligenceResponse requestFreshIntelligence(
            Long missionId
    ) {

        Mission mission = missionService.getMissionById(missionId);

        AiFreshIntelligenceResponse response =
                aiClient.requestFreshIntelligence(missionId);

        logDecision(
                mission,
                "FRESH_INTELLIGENCE_REQUESTED",
                response.getStatus(),
                response
        );

        missionService.updateStatus(
                missionId,
                Mission.MissionStatus.AWAITING_PAYMENT
        );

        return response;
    }

    /**
     * Final AI decision after intelligence has been verified.
     */
    @Transactional
    public AiDecisionResponse finalizeMission(Long missionId) {

        Mission mission = missionService.getMissionById(missionId);

        AiDecisionResponse response =
                aiClient.decision(missionId);

        logDecision(
                mission,
                "FINAL_DECISION",
                response.getReason(),
                response
        );

        missionService.updateStatus(
                missionId,
                Mission.MissionStatus.COMPLETED
        );

        return response;
    }

    private void logDecision(
            Mission mission,
            String eventType,
            String message,
            Object metadataObject
    ) {

        DecisionEvent event = new DecisionEvent();

        event.setMission(mission);
        event.setEventType(eventType);
        event.setMessage(message != null ? message : "");
        event.setMetadata(toJson(metadataObject));

        decisionEventRepository.save(event);
    }

    private String toJson(Object obj) {

        try {
            return objectMapper.writeValueAsString(obj);

        } catch (JacksonException e) {

            return "{\"error\":\"failed to serialize metadata\"}";
        }
    }
}