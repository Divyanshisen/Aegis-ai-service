package com.aegis.aegis_backend.repository;


import com.aegis.aegis_backend.entity.Bounty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BountyRepository extends JpaRepository<Bounty, Long> {
    List<Bounty> findByMissionId(Long missionId);
}