package com.casey.applyflow.model.builder;

import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.enums.Role;

public class JobBoardMemberBuilder {
    private User user;
    private Role role;

    public JobBoardMemberBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public JobBoardMemberBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public JobBoardMember build() {
        return new JobBoardMember(
            user, 
            role
        );
    }
}
