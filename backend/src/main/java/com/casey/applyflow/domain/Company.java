package com.casey.applyflow.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String name;

    @Column()
    private String location;

    @Column()
    private Double rating;

    @OneToMany(mappedBy = "company")
    private List<Contact> interviewers = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    private List<Application> applications = new ArrayList<>();

    protected Company() {} // JPA Constructor

    public Company(String name, String location, Double rating) {
        this.name = name;
        this.location = location;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Contact> getInterviewers() {
        return interviewers;
    }

    public void addInterviewer(Contact contact) {
        interviewers.add(contact);
        contact.setCompany(this);
    }

    public void removeInterviewer(Contact contact) {
        interviewers.remove(contact);
        contact.setCompany(null);
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void addApplication(Application application) {
        applications.add(application);
    }

    public void removeApplication(Application application) {
        applications.remove(application);
    }

    public Long getId() {
        return id;
    }
}