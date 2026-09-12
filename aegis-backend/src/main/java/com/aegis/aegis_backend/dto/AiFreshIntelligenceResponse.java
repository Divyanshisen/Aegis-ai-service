package com.aegis.aegis_backend.dto;

public class AiFreshIntelligenceResponse {

    private Long missionId;
    private String status;

    public AiFreshIntelligenceResponse() {}

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}