package com.aegis.aegis_backend.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bounties")
public class Bounty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(nullable = false, length = 1000)
    private String task;

    @Column(nullable = false)
    private Double reward;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BountyStatus status = BountyStatus.OPEN;

    private String contributor; // nullable until claimed

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum BountyStatus { OPEN, CLAIMED, COMPLETED, CANCELLED }

    public Bounty() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    public Double getReward() { return reward; }
    public void setReward(Double reward) { this.reward = reward; }
    public BountyStatus getStatus() { return status; }
    public void setStatus(BountyStatus status) { this.status = status; }
    public String getContributor() { return contributor; }
    public void setContributor(String contributor) { this.contributor = contributor; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}