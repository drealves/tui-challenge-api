package com.core.tuichallengeapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "github.api")
public class GitHubApiPropertiesConfig {

    private String baseUrl;
    private String token;

    public String getBaseUrl() { return baseUrl; }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}