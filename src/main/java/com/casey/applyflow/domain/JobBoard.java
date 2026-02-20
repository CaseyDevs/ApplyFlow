package com.casey.applyflow.domain;

import java.util.List;

import com.casey.applyflow.domain.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

    @Column(nullable = false)
    private List<JobBoardMember> members;

    public JobBoard(String title, User owner, List<JobBoardMember> members) {
        this.title = title;
        this.members = members;
    }

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
            .orElse(null);
    }

    public List<JobBoardMember> getMembers() {
        return members;
    }

    public void addMember(JobBoardMember newMember) {
        members.add(newMember);
    }

    public void removeMember(JobBoardMember member) {
        members.remove(member);
    }
}
