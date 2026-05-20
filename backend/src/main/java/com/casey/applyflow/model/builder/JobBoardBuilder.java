package com.casey.applyflow.model.builder;

import java.util.ArrayList;
import java.util.List;

import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;

public class JobBoardBuilder {
    private String title;
    private JobBoardMember owner;
    private List<JobBoardMember> membersToAdd = new ArrayList<>();

    public JobBoardBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public JobBoardBuilder withOwner(JobBoardMember owner) {
        this.owner = owner;
        return this;
    }

    // add member to list
    public JobBoardBuilder addMember(JobBoardMember member) {
        this.membersToAdd.add(member);  // ← Store it, don't recurse
        return this;
    }

    public JobBoard build() {
        if (owner == null) {
            throw new IllegalArgumentException("Owner is required");
        }

        JobBoard jobBoard = new JobBoard(title, owner);
        
        // Add all members after construction
        for (JobBoardMember member : membersToAdd) {
            jobBoard.addMember(member);
        }
        
        return jobBoard;
    }
}
