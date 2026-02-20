package com.casey.applyflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.JobBoardMember;

public interface JobBoardMemberRepository extends JpaRepository<JobBoardMember, Long> {
    JobBoardMember findByIdAndJobBoardId(Long jobBoardMemberId, Long jobBoardId);
}
