package com.core.tuichallengeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.core")
public class TuiChallengeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuiChallengeApiApplication.class, args);
    }

}
