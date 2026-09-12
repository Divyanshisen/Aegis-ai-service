package com.aegis.aegis_backend.dto;


public class ClaimBountyRequest {
    @jakarta.validation.constraints.NotBlank(message = "contributor is required")
    private String contributor;

    public ClaimBountyRequest() {}
    public String getContributor() { return contributor; }
    public void setContributor(String contributor) { this.contributor = contributor; }
}