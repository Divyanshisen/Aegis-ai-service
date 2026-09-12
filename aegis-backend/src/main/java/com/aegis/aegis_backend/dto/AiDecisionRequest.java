package com.aegis.aegis_backend.dto;

public class AiDecisionRequest {

    private Long missionId;

    public AiDecisionRequest() {}

    public AiDecisionRequest(Long missionId) {
        this.missionId = missionId;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }
}