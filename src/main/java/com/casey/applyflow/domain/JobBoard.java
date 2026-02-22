package com.casey.applyflow.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.casey.applyflow.domain.enums.Role;
import com.casey.applyflow.exception.NoOwnerException;

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
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name="job_board")
public class JobBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(nullable = true)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "jobBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobBoardMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "jobBoard")
    private Set<Application> applications = new HashSet<>();

    public JobBoard(
        String title,
        JobBoardMember owner,
        Set<Application> applications
    ) {
        this.title = title;
        this.applications = applications != null ? applications : new HashSet<>();
        if (owner != null) {
            this.user = owner.getUser();
        }
    }

    protected JobBoard() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JobBoardMember getOwner() {
        return members.stream()
            .filter(member -> member.getRole() == Role.OWNER)
            .findFirst()
            .orElseThrow(() -> new NoOwnerException("Job board must have an owner"));
    }

    public void setOwner(JobBoardMember member) {
        // reset current owners role
        if (this.getOwner() != null) {   
            this.getOwner().setRole(Role.MEMBER);
        }

        member.setRole(Role.OWNER);
    }

    public List<JobBoardMember> getMembers() {
        return members;
    }

    public void addMember(JobBoardMember newMember) {
        newMember.setJobBoard(this);
        members.add(newMember);
    }

    public void removeMember(JobBoardMember member) {
        member.setJobBoard(null);
        members.remove(member);
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public void addApplication(Application application) {
        application.setJobBoard(this);
        applications.add(application);
    }

    public void removeApplication(Application application) {
        application.setJobBoard(null);
        applications.remove(application);
    }
}
