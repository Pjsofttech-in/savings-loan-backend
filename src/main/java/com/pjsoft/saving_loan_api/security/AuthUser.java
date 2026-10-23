package com.pjsoft.saving_loan_api.security;

/** The signed-in person, taken from a verified login token. */
public record AuthUser(String role, Long memberId, String name) {

    public static final String ADMIN = "ADMIN";
    public static final String MEMBER = "MEMBER";

    public boolean isAdmin() {
        return ADMIN.equals(role);
    }

    public boolean isMember() {
        return MEMBER.equals(role);
    }
}
