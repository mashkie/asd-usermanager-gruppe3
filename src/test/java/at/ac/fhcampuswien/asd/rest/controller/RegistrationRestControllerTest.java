package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.entity.repository.UserRepository;
import at.ac.fhcampuswien.asd.rest.mapper.UserMapper;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationRestControllerTest {

    final String password = "password";
    final String firstname = "firstname";
    final String lastname = "lastname";
    final String username = "username";
    @Autowired
    UserMapper userMapper;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void successfulRegistration() throws Exception {

        // Arrange
        InboundUserRegistrationDto inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .password(password)
                .firstName(firstname)
                .lastName(lastname)
                .build();

        // Act && Assert
        this.mockMvc.perform(post("/users/register").content(mapper.writeValueAsString(inboundUserRegistrationDto))
                        .header("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(mapper.writeValueAsString(
                        userMapper.modelToOutboundDto(userRepository.findByUsername(username)))))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void userAlreadyExists() throws Exception {
        // Arrange
        InboundUserRegistrationDto inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .password(password)
                .firstName(firstname)
                .lastName(lastname)
                .build();

        // Act && Assert
        this.mockMvc.perform(post("/users/register").content(mapper.writeValueAsString(inboundUserRegistrationDto))
                .header("Content-Type", "application/json"));
        this.mockMvc.perform(post("/users/register").content(mapper.writeValueAsString(inboundUserRegistrationDto))
                        .header("Content-Type", "application/json"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{\"message\":\"The username is already taken!\"}"))
                .andDo(print());
    }

    @Test
    void erroneousInput() throws Exception {
        // Arrange
        InboundUserRegistrationDto inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .lastName(lastname)
                .build();


        // Act && Assert
        this.mockMvc.perform(post("/users/register").content(mapper.writeValueAsString(inboundUserRegistrationDto))
                        .header("Content-Type", "application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
    }
}