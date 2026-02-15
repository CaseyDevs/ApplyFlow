package com.casey.applyflow.repository;

import org.springframework.data.jpa.domain.Specification;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.User;

public class ApplicationSpecification {
    public static Specification<Application> companyName(String companyName) {
        return (root, query, cb) ->
            companyName == null ? null : cb.equal(
                cb.lower(root.get("company").get("name")),
                companyName.toLowerCase()
            );
    }

    public static Specification<Application> companyId(Long companyId) {
        return (root, query, cb) ->
            companyId == null ? null : cb.equal(root.get("company").get("id"), companyId);
    }

    public static Specification<Application> hasInterview(Boolean hasInterview) {
        return (root, query, cb) ->
            hasInterview == null ? null :
                (hasInterview ? cb.isNotEmpty(root.get("interviews")) : cb.isEmpty(root.get("interviews")));
    }

    public static Specification<Application> belongsToUser(User user) {
        return (root, query, cb) ->
            user == null ? null : cb.equal(root.get("user"), user);
    }
}
