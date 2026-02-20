package com.casey.applyflow.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name="jobboard")
public class JobBoard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(nullable = true)
    private String title;

    @Column(nullable = false)
    private User owner;

    @Column(nullable = false)
    private List<User> members;

    public JobBoard(String title, User owner, List<User> members) {
        this.title = title;
        this.owner = owner;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getMembers() {
        return members;
    }

    public void addMember(User newMember) {
        members.add(newMember);
    }

    public void removeMember(User member) {
        members.remove(member);
    }
}
