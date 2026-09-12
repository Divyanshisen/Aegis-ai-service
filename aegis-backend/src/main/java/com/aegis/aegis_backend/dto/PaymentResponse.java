package com.aegis.aegis_backend.dto;



import com.aegis.aegis_backend.entity.Payment;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long missionId;
    private Double amount;
    private String asset;
    private String network;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
    private Long confirmedRound;
    private Boolean onChainVerified;
    private String loraUrl;

    public PaymentResponse() {}

    public static PaymentResponse fromEntity(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.id = payment.getId();
        response.missionId = payment.getMission().getId();
        response.amount = payment.getAmount();
        response.asset = payment.getAsset();
        response.network = payment.getNetwork();
        response.status = payment.getStatus().name();
        response.transactionId = payment.getTransactionId();
        response.createdAt = payment.getCreatedAt();
        response.confirmedRound = payment.getConfirmedRound();
        response.onChainVerified = payment.getOnChainVerified();
        response.loraUrl = payment.getTransactionId() != null
                ? "https://lora.algokit.io/testnet/transaction/" + payment.getTransactionId()
                : null;
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getNetwork() { return network; }
    public void setNetwork(String network) { this.network = network; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getConfirmedRound() { return confirmedRound; }
    public void setConfirmedRound(Long confirmedRound) { this.confirmedRound = confirmedRound; }
    public Boolean getOnChainVerified() { return onChainVerified; }
    public void setOnChainVerified(Boolean onChainVerified) { this.onChainVerified = onChainVerified; }
    public String getLoraUrl() { return loraUrl; }
    public void setLoraUrl(String loraUrl) { this.loraUrl = loraUrl; }

}