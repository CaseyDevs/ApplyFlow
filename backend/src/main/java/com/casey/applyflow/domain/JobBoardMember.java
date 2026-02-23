package com.casey.applyflow.domain;

import com.casey.applyflow.domain.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_board_member")
public class JobBoardMember {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private JobBoard jobBoard;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    public JobBoardMember(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    protected JobBoardMember() {}

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setJobBoard(JobBoard jobBoard) {
        this.jobBoard = jobBoard;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
