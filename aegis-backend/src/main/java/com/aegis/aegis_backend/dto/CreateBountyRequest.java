package com.aegis.aegis_backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateBountyRequest {
    @NotBlank(message = "task is required")
    private String task;

    @NotNull(message = "reward is required")
    @Positive(message = "reward must be greater than 0")
    private Double reward;

    public CreateBountyRequest() {}
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    public Double getReward() { return reward; }
    public void setReward(Double reward) { this.reward = reward; }
}