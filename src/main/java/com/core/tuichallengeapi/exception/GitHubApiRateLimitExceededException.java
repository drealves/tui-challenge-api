package com.core.tuichallengeapi.exception;

public class GitHubApiRateLimitExceededException extends RuntimeException {

    public GitHubApiRateLimitExceededException(String message) {
        super(message);
    }

}