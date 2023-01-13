package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.entity.repository.UserRepository;
import at.ac.fhcampuswien.asd.exceptions.UserAlreadyExistsException;
import at.ac.fhcampuswien.asd.rest.model.InboundUserLoginDto;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class SessionRestControllerTest {

    final String correctPassword = "password";
    final String incorrectPassword = "1234";
    final String username = "username";
    final String firstname = "firstname";
    final String lastname = "lastname";
    private final String sessionFieldName = "X-SESSION-ID";
    @Autowired
    private HttpSession httpSession;
    private UUID sessionId;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRestService userRestService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws UserAlreadyExistsException {

        sessionId = UUID.randomUUID();
        InboundUserRegistrationDto inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .password(correctPassword)
                .firstName(firstname)
                .lastName(lastname)
                .build();
        userRestService.createUser(inboundUserRegistrationDto);
    }

    @AfterEach
    void tearDown() {
        httpSession.removeAttribute(sessionFieldName);
        userRepository.deleteAll();
    }

    @Test
    void successfulLogin() throws Exception {

        // Arrange
        InboundUserLoginDto inboundUserLoginDto = InboundUserLoginDto.builder()
                .username(username)
                .password(correctPassword)
                .build();

        // Act && Assert
        HttpSession httpSession = this.mockMvc.perform(
                        post("/users/login").content(mapper.writeValueAsString(inboundUserLoginDto))
                                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print())
                .andReturn()
                .getRequest()
                .getSession();

        Assertions.assertNotNull(httpSession.getAttribute(sessionFieldName));
    }

    @Test
    void invalidPasswordOnLogin() throws Exception {

        // Arrange
        InboundUserLoginDto inboundUserLoginDto = InboundUserLoginDto.builder()
                .username(username)
                .password(incorrectPassword)
                .build();

        // Act && Assert
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .content(mapper.writeValueAsString(inboundUserLoginDto))
                        .header("Content-Type", "application/json"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{\"message\":\"Username or password not correct\"}"))
                .andDo(print());
    }

    @Test
    void incompleteInputOnLogin() throws Exception {

        // Arrange
        InboundUserLoginDto inboundUserLoginDto = InboundUserLoginDto.builder()
                .username(username)
                .build();

        // Act && Assert
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .content(mapper.writeValueAsString(inboundUserLoginDto))
                        .header("Content-Type", "application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());


    }

    @Test
    void successfulLogout() throws Exception {

        // Arrange
        httpSession.setAttribute(sessionFieldName, sessionId);
        userRestService.login(username, correctPassword, httpSession);
        UUID sessionId = userRepository.findByUsername(username)
                .getSession();

        // Act && Assert
        HttpSession responseSession = this.mockMvc.perform(post("/users/{username}/logout", username)
                        .sessionAttr(sessionFieldName, sessionId))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn()
                .getRequest()
                .getSession();
        Assertions.assertNull(responseSession.getAttribute(sessionFieldName));
    }


    @Test
    void incorrectSessionOnLogout() throws Exception {
        // Arrange
        httpSession.setAttribute(sessionFieldName, sessionId);
        userRestService.login(username, correctPassword, httpSession);

        // Act && Assert
        this.mockMvc.perform(
                        post("/users/{username}/logout", username).sessionAttr(sessionFieldName, UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}