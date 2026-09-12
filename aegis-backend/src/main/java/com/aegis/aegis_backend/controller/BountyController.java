package com.aegis.aegis_backend.controller;


import com.aegis.aegis_backend.dto.BountyResponse;
import com.aegis.aegis_backend.dto.ClaimBountyRequest;
import com.aegis.aegis_backend.dto.CreateBountyRequest;
import com.aegis.aegis_backend.entity.Bounty;
import com.aegis.aegis_backend.service.BountyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BountyController {

    private final BountyService bountyService;

    public BountyController(BountyService bountyService) {
        this.bountyService = bountyService;
    }

    @PostMapping("/api/missions/{missionId}/bounties")
    public ResponseEntity<BountyResponse> createBounty(
            @PathVariable Long missionId, @Valid @RequestBody CreateBountyRequest request) {
        Bounty bounty = bountyService.createBounty(missionId, request);
        return new ResponseEntity<>(BountyResponse.fromEntity(bounty), HttpStatus.CREATED);
    }

    @GetMapping("/api/missions/{missionId}/bounties")
    public ResponseEntity<List<BountyResponse>> getBounties(@PathVariable Long missionId) {
        List<BountyResponse> responses = bountyService.getBountiesForMission(missionId)
                .stream().map(BountyResponse::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/api/bounties/{id}/claim")
    public ResponseEntity<BountyResponse> claimBounty(
            @PathVariable Long id, @Valid @RequestBody ClaimBountyRequest request) {
        Bounty bounty = bountyService.claimBounty(id, request.getContributor());
        return ResponseEntity.ok(BountyResponse.fromEntity(bounty));
    }

    @PostMapping("/api/bounties/{id}/complete")
    public ResponseEntity<BountyResponse> completeBounty(@PathVariable Long id) {
        Bounty bounty = bountyService.completeBounty(id);
        return ResponseEntity.ok(BountyResponse.fromEntity(bounty));
    }
}