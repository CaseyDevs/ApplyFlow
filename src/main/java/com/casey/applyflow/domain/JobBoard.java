package com.casey.applyflow.domain;

import java.util.List;

import com.casey.applyflow.domain.enums.Role;
import com.casey.applyflow.exception.NoOwnerException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name="job_board")
public class JobBoard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(nullable = true)
    private String title;

    
    @OneToMany(mappedBy = "jobBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobBoardMember> members;

    public JobBoard(String title, JobBoardMember owner, List<JobBoardMember> members) {
        this.title = title;
        this.members = members;
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
}
