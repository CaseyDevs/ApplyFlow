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
import jakarta.persistence.Version;

@Entity
@Table(name = "job_board_application")
public class JobBoardApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User addedBy;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_board_id")
    private JobBoard jobBoard;

    @OneToMany(mappedBy = "jobBoardApplication", cascade = CascadeType.ALL, orphanRemoval = true) // remove statuses on application deletion
    List<JobBoardApplicationStatus> statusList = new ArrayList<>(); 

    protected JobBoardApplication() {} // for jpa

    public JobBoardApplication(
        User addedBy,
        Application application,
        JobBoard jobBoard
    ) {
        this.addedBy = addedBy;
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

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
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
