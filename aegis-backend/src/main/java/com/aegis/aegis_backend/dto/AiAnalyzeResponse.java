package com.aegis.aegis_backend.dto;



import java.util.List;

public class AiAnalyzeResponse {

    private List<String> requiredIntelligence;
    private Double confidence;
    private Boolean needsFreshData;
    private String reason;

    public AiAnalyzeResponse() {}

    public List<String> getRequiredIntelligence() {
        return requiredIntelligence;
    }

    public void setRequiredIntelligence(List<String> requiredIntelligence) {
        this.requiredIntelligence = requiredIntelligence;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Boolean getNeedsFreshData() {
        return needsFreshData;
    }

    public void setNeedsFreshData(Boolean needsFreshData) {
        this.needsFreshData = needsFreshData;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}