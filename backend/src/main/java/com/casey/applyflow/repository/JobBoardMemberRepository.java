package com.casey.applyflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.model.JobBoardMember;

public interface JobBoardMemberRepository extends JpaRepository<JobBoardMember, Long> {
    Optional<JobBoardMember> findByIdAndJobBoardId(Long jobBoardMemberId, Long jobBoardId);
    Optional<JobBoardMember> findByJobBoardIdAndUserId(Long jobBoardId, Long userId);
}
