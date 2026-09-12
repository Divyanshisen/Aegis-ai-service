package com.aegis.aegis_backend.service;


import com.aegis.aegis_backend.dto.CreateBountyRequest;
import com.aegis.aegis_backend.entity.Bounty;
import com.aegis.aegis_backend.entity.Mission;
import com.aegis.aegis_backend.exception.ResourceNotFoundException;
import com.aegis.aegis_backend.repository.BountyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BountyService {

    private final BountyRepository bountyRepository;
    private final MissionService missionService;

    public BountyService(BountyRepository bountyRepository, MissionService missionService) {
        this.bountyRepository = bountyRepository;
        this.missionService = missionService;
    }

    @Transactional
    public Bounty createBounty(Long missionId, CreateBountyRequest req) {
        Mission mission = missionService.getMissionById(missionId);
        Bounty bounty = new Bounty();
        bounty.setMission(mission);
        bounty.setTask(req.getTask());
        bounty.setReward(req.getReward());
        bounty.setStatus(Bounty.BountyStatus.OPEN);
        return bountyRepository.save(bounty);
    }

    public List<Bounty> getBountiesForMission(Long missionId) {
        missionService.getMissionById(missionId);
        return bountyRepository.findByMissionId(missionId);
    }

    @Transactional
    public Bounty claimBounty(Long bountyId, String contributor) {
        Bounty bounty = getBountyById(bountyId);
        if (bounty.getStatus() != Bounty.BountyStatus.OPEN) {
            throw new IllegalStateException("Bounty is not open for claiming");
        }
        bounty.setContributor(contributor);
        bounty.setStatus(Bounty.BountyStatus.CLAIMED);
        return bountyRepository.save(bounty);
    }

    @Transactional
    public Bounty completeBounty(Long bountyId) {
        Bounty bounty = getBountyById(bountyId);
        bounty.setStatus(Bounty.BountyStatus.COMPLETED);
        return bountyRepository.save(bounty);
    }

    public Bounty getBountyById(Long id) {
        return bountyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bounty not found with id: " + id));
    }
}
