package com.core.tuichallengeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Owner {
    private String login;

    public Owner() {
        // Default constructor
    }

    public Owner(String login) {
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