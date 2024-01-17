package com.core.tuichallengeapi.model.dto;

public class OwnerDto {
    private String login;

    public OwnerDto() {
        // Default constructor
    }

    public OwnerDto(String login) {
        this.login = login;
    }
    // Getters and setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
