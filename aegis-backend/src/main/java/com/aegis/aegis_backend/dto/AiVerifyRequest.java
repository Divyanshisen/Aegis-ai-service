package com.aegis.aegis_backend.dto;

import java.util.List;

public class AiVerifyRequest {

    private Long missionId;
    private List<IntelligenceSnapshot> intelligence;

    public AiVerifyRequest() {}

    public AiVerifyRequest(Long missionId, List<IntelligenceSnapshot> intelligence) {
        this.missionId = missionId;
        this.intelligence = intelligence;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public List<IntelligenceSnapshot> getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(List<IntelligenceSnapshot> intelligence) {
        this.intelligence = intelligence;
    }
}