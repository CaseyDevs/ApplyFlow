package com.casey.applyflow.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.casey.applyflow.model.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "job_board_application_status",
    // ensure a given user can only have at most one status row per job board / application pair
    uniqueConstraints = @UniqueConstraint(columnNames = {"job_board_id", "application_id", "user_id"})
)
public class JobBoardApplicationStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_board_id", nullable = false)
    private JobBoard jobBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String updatedBy;

    protected JobBoardApplicationStatus() {}; // for JPA

    public JobBoardApplicationStatus(
        JobBoard jobBoard,
        Application application,
        User user,
        Status status
    ) {
        this.jobBoard = jobBoard;
        this.application = application;
        this.user = user;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = user.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobBoard getJobBoard() {
        return jobBoard;
    }

    public void setJobBoard(JobBoard jobBoard) {
        this.jobBoard = jobBoard;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobBoardApplicationStatus)) return false;
        JobBoardApplicationStatus that = (JobBoardApplicationStatus) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
