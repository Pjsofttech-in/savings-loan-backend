package com.pjsoft.saving_loan_api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static AuthUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser) {
            return (AuthUser) authentication.getPrincipal();
        }
        return null;
    }

    /** Members may only see their own records; administrators may see the requested member (or all when null). */
    public static Long scopeMemberId(Long requested) {
        AuthUser user = get();
        if (user != null && user.isMember()) {
            return user.memberId();
        }
        return requested;
    }
}
