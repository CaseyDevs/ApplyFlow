package com.casey.applyflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.casey.applyflow.model.JobBoardApplication;
import com.casey.applyflow.model.JobBoardApplicationStatus;

public interface JobBoardApplicationStatusRepository extends JpaRepository<JobBoardApplicationStatus, Long> {
    List<JobBoardApplicationStatus> findAllByJobBoardApplication(JobBoardApplication jobBoardApplication);
    List<JobBoardApplicationStatus> findAllByJobBoardApplicationJobBoardIdAndUserId(Long jobBoardId, Long userId);

    Optional<JobBoardApplicationStatus> findByJobBoardApplicationIdAndUserId(Long jobBoardApplicationId,Long userId );

    // custom query for removing all statuses for job baord applications in a given job board (user exclusive)
    @Modifying
    @Query("DELETE FROM JobBoardApplicationStatus s WHERE s.jobBoardApplication.jobBoard.id = :jobBoardId AND s.user.id = :userId")
    int deleteAllByJobBoardIdAndUserId(@Param("jobBoardId") Long jobBoardId, @Param("userId") Long userId);
}