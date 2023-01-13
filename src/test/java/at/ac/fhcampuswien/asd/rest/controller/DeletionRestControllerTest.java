package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.entity.repository.UserRepository;
import at.ac.fhcampuswien.asd.entity.services.UserEntityService;
import at.ac.fhcampuswien.asd.exceptions.UserAlreadyExistsException;
import at.ac.fhcampuswien.asd.rest.mapper.UserMapper;
import at.ac.fhcampuswien.asd.rest.model.DeleteUserRequest;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeletionRestControllerTest {


    final String password = "password";
    final String firstname = "firstname";
    final String lastname = "lastname";
    final String username = "username";
    final String X_SESSION_ID = "X-SESSION-ID";

    // End points
    final String REMOVE_USER_PATH = "/users/{username}";
    final String LOGIN_PATH = "/users/login";
    // Response Types
    final String APPLICATION_JSON = "application/json";
    final String CONTENT_TYPE = "Content-Type";

    @Autowired
    UserMapper userMapper;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRestService userRestService;

    static UUID sessionId;
    private UUID sessionID = null;

    @BeforeEach
    void setUp() throws Exception {
        registerUser();
        loginUser();
    }

    private void loginUser() throws Exception {
        // Act && Assert
        HttpSession httpSession = this.mockMvc.perform(post(LOGIN_PATH)
                        .content(mapper.writeValueAsString(getInboundUserRegistrationDto()))
                        .header(CONTENT_TYPE, APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print())
                .andReturn()
                .getRequest()
                .getSession();
        sessionID = (UUID) httpSession.getAttribute(X_SESSION_ID);
    }

    private void registerUser() throws UserAlreadyExistsException {
        InboundUserRegistrationDto inboundUserRegistrationDto = getInboundUserRegistrationDto();
        userRestService.createUser(inboundUserRegistrationDto);
        sessionId = UUID.randomUUID();
    }

    private InboundUserRegistrationDto getInboundUserRegistrationDto() {
        return InboundUserRegistrationDto.builder()
                .username(username)
                .password(password)
                .firstName(firstname)
                .lastName(lastname)
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    void delete_userIsLoggedIn() throws Exception {
        // Arrange
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setCurrentPassword(password);

        HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
        sessionAttributes.put(X_SESSION_ID, sessionID);
        // Act && Assert
        this.mockMvc.perform(delete(REMOVE_USER_PATH, username).sessionAttrs(sessionAttributes)
                        .content(mapper.writeValueAsString(deleteUserRequest))
                        .header(CONTENT_TYPE, APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void delete_userIsIncorrect() throws Exception {
        // Arrange
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setCurrentPassword("test");

        HashMap<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(X_SESSION_ID, sessionID);
        // Act && Assert
        this.mockMvc.perform(delete(REMOVE_USER_PATH, "test").sessionAttrs(sessionAttributes)
                        .content(mapper.writeValueAsString(deleteUserRequest))
                        .header(CONTENT_TYPE, APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"The user name does not exist!\"}"))
                .andDo(print());
    }

    @Test
    void delete_sessionIsIncorrect() throws Exception {
        // Arrange
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setCurrentPassword(username);

        HashMap<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(X_SESSION_ID, UUID.randomUUID());
        // Act && Assert
        this.mockMvc.perform(delete(REMOVE_USER_PATH, username).sessionAttrs(sessionAttributes)
                        .content(mapper.writeValueAsString(deleteUserRequest))
                        .header(CONTENT_TYPE, APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"You are not authorized to delete the account.\"}"))
                .andDo(print());
    }

    @Test
    void delete_passwordIsIncorrect() throws Exception {
        // Arrange
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setCurrentPassword("Demoo");

        HashMap<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(X_SESSION_ID, sessionID);
        // Act && Assert
        this.mockMvc.perform(delete(REMOVE_USER_PATH, username).sessionAttrs(sessionAttributes)
                        .content(mapper.writeValueAsString(deleteUserRequest))
                        .header(CONTENT_TYPE, APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"Passwords do not match\"}"))
                .andDo(print());
    }

}