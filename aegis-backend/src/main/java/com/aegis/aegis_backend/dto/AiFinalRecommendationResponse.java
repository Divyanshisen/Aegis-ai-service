package com.aegis.aegis_backend.dto;


public class AiFinalRecommendationResponse {
    private String recommendation;
    private Double confidence;
    private String reasoning;

    public AiFinalRecommendationResponse() {}

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
}