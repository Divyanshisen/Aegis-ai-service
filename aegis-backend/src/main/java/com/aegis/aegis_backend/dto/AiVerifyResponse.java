package com.aegis.aegis_backend.dto;

public class AiVerifyResponse {

    private boolean verified;
    private double confidence;
    private String recommendation;

    public AiVerifyResponse() {}

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}