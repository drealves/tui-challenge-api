package com.core.tuichallengeapi.controller;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.core.tuichallengeapi.service.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
public class GitHubControllerTests {

    private static final String APPLICATION_JSON = "application/json";

    private static final String APPLICATION_XML = "application/xml";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private GitHubService gitHubService;

    @Test
    void whenUserExists_thenStatus200() throws Exception {
        // Setup Mock responses from the service

        mockMvc.perform(get("/api/github/repositories/{username}", "user")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
        // Add more assertions based on the expected JSON structure
    }

    @Test
    void whenUserNotFound_thenStatus404() throws Exception {
        // Setup Mock to throw UserNotFoundException

        mockMvc.perform(get("/api/github/repositories/{username}", "nonexistentUser")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        // Add more assertions for error message
    }

    @Test
    void whenAcceptHeaderIsXml_thenStatus406() throws Exception {
        mockMvc.perform(get("/api/github/repositories/{username}", "user")
                        .accept(APPLICATION_XML))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status").value(406));
    }

    // Setup additional tests
}
