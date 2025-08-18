package com.passtival.backend.global.common.enums;


public enum Role {
    USER, ADMIN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}