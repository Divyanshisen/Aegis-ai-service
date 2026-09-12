package com.aegis.aegis_backend.dto;


import com.aegis.aegis_backend.entity.Bounty;
import java.time.LocalDateTime;

public class BountyResponse {
    private Long id;
    private Long missionId;
    private String task;
    private Double reward;
    private String status;
    private String contributor;
    private LocalDateTime createdAt;

    public BountyResponse() {}

    public static BountyResponse fromEntity(Bounty bounty) {
        BountyResponse r = new BountyResponse();
        r.id = bounty.getId();
        r.missionId = bounty.getMission().getId();
        r.task = bounty.getTask();
        r.reward = bounty.getReward();
        r.status = bounty.getStatus().name();
        r.contributor = bounty.getContributor();
        r.createdAt = bounty.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    public Double getReward() { return reward; }
    public void setReward(Double reward) { this.reward = reward; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getContributor() { return contributor; }
    public void setContributor(String contributor) { this.contributor = contributor; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}