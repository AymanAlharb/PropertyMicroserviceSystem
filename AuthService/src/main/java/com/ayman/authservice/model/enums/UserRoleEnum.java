package com.ayman.authservice.model.enums;

public enum UserRoleEnum {
    BUYER, SELLER, BROKER;

    public static UserRoleEnum getCode(String role) {
        return switch (role.toUpperCase()) {
            case "BUYER" -> BUYER;
            case "SELLER" -> SELLER;
            default -> BROKER;
        };
    }
}
