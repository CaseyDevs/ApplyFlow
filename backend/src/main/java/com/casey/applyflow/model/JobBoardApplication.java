package com.casey.applyflow.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "job_board_application", uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "job_board_id"}))
public class JobBoardApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_board_member_id")
    private JobBoardMember jobBoardMember;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_board_id")
    private JobBoard jobBoard;

    @OneToMany(mappedBy = "jobBoardApplication", cascade = CascadeType.ALL, orphanRemoval = true) // remove statuses on application deletion
    List<JobBoardApplicationStatus> statusList = new ArrayList<>(); 

    protected JobBoardApplication() {} // for jpa

    public JobBoardApplication(
        JobBoardMember jobBoardMember,
        Application application,
        JobBoard jobBoard
    ) {
        this.jobBoardMember = jobBoardMember;
        this.application = application;
        this.jobBoard = jobBoard;
        addedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public JobBoardMember getJobBoardMember() {
        return jobBoardMember;
    }

    public void setJobBoardMember(JobBoardMember jobBoardMember) {
        this.jobBoardMember = jobBoardMember;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public JobBoard getJobBoard() {
        return jobBoard;
    }

    public void setJobBoard(JobBoard jobBoard) {
        this.jobBoard = jobBoard;
    }

    public List<JobBoardApplicationStatus> getStatusList() {
        return statusList;
    }

    public void addUserStatus(JobBoardApplicationStatus status) {
        statusList.add(status);
    }

    public void removeUserStatus(JobBoardApplicationStatus status) {
        statusList.remove(status);
    }
}   
