package at.ac.fhcampuswien.asd.rest.service;

import at.ac.fhcampuswien.asd.entity.models.User;
import at.ac.fhcampuswien.asd.entity.services.UserEntityService;
import at.ac.fhcampuswien.asd.exceptions.*;
import at.ac.fhcampuswien.asd.rest.mapper.UserMapper;
import at.ac.fhcampuswien.asd.rest.model.InboundUserChangePasswordDto;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.model.OutboundUserRegistrationDto;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@SpringBootTest
class UserRestServiceTest {

    static UUID sessionId;
    private final long sessionValidUntil = new Date().getTime() + Duration.ofHours(1)
            .toMillis();
    String password = "password";
    String username = "username";
    String firstname = "thomas";
    String lastname = "scheibelhofer";
    @Mock
    UserEntityService userEntityService;
    @InjectMocks
    UserRestService userRestService;
    User user;
    @Mock
    private HttpSession httpSession;

    @BeforeAll
    public static void setUpUUID() {
        sessionId = UUID.randomUUID();
    }

    @BeforeEach
    void setUp() {
        // Create the user in the DB
        user = User.builder()
                .username(username)
                .firstName(firstname)
                .lastName(lastname)
                .session(sessionId)
                .sessionValidUntil(sessionValidUntil)
                .build();
        user.setPassword(password);
        Mockito.when(httpSession.getAttribute("X-SESSION-ID"))
                .thenReturn(sessionId);

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void comparePassword() {
        // Act
        boolean passwordsAreEqual = userRestService.comparePassword(user, password);

        // Assert
        Assertions.assertTrue(passwordsAreEqual);

    }

    @Test
    void sessionValid() {

        // Act
        boolean sessionIsValid;
        try {
            sessionIsValid = userRestService.sessionStillValid(user);
        } catch (InvalidSessionException e) {
            throw new RuntimeException(e);
        }

        // Assert
        Assertions.assertTrue(sessionIsValid);

    }

    @Test
    void logoutUserOnInvalidSession() {
        user.setSessionValidUntil(null);
        // Act && Assert
        Assertions.assertThrows(InvalidSessionException.class, () -> userRestService.logoutUserOnInvalidSession(user));

    }

    @Test
    void login() {

        // Arrange
        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(user);

        // Act && Assert
        Assertions.assertDoesNotThrow(() -> userRestService.login(username, password, httpSession));

    }

    @Test
    void loginWithInvalidPassword() {
        Assertions.assertThrows(AuthenticationException.class,
                () -> userRestService.login(username, "wrongPassword", httpSession));
    }

    @Test
    void loginWithLockedUser() {

        user.setLockedUntil(new Date().getTime() * 2);

        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(user);

        Assertions.assertThrows(UserLockedException.class,
                () -> userRestService.login(username, password, httpSession));
    }

    @Test
    void logout() {
        // Arrange
        boolean userIsLoggedOut;
        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(user);

        // Act
        try {
            userIsLoggedOut = userRestService.logout(username, httpSession);
        } catch (InvalidSessionException | UserNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Assert
        Assertions.assertTrue(userIsLoggedOut);
    }

    @Test
    void logoutWithInvalidSession() {
        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(user);
        Mockito.when(httpSession.getAttribute("X-SESSION-ID"))
                .thenReturn(UUID.randomUUID());

        Assertions.assertThrows(InvalidSessionException.class, () -> userRestService.logout(username, httpSession));
    }

    @Test
    void logoutWithInvalidUsername() {
        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(user);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userRestService.logout("wrongUsername", httpSession));
    }

    @Test
    void createUser() {

        //Arrange
        UserMapper userMapper = new UserMapper();
        InboundUserRegistrationDto inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .password(password)
                .firstName(firstname)
                .lastName(lastname)
                .build();

        OutboundUserRegistrationDto outboundUserRegistrationDto = userMapper.modelToOutboundDto(user);

        try {
            Mockito.when(userEntityService.addUser(inboundUserRegistrationDto))
                    .thenReturn(outboundUserRegistrationDto);
        } catch (UserAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

        // Act
        OutboundUserRegistrationDto createUser;
        try {
            createUser = userRestService.createUser(inboundUserRegistrationDto);
        } catch (UserAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

        // Assert
        Assertions.assertEquals(createUser, outboundUserRegistrationDto);
    }

    @Test
    void createUserWithExistingUsername() {
        //Arrange
        InboundUserRegistrationDto inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .password(password)
                .firstName(firstname)
                .lastName(lastname)
                .build();


        try {
            Mockito.when(userEntityService.addUser(inboundUserRegistrationDto))
                    .thenThrow(UserAlreadyExistsException.class);
        } catch (UserAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

        // Act
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userRestService.createUser(inboundUserRegistrationDto));
    }


    @Test
    void changePassword() {
        // Arrange
        InboundUserChangePasswordDto inboundUserChangePasswordDto = InboundUserChangePasswordDto.builder()
                .oldPassword("password")
                .newPassword("newPassword")
                .controlNewPassword("newPassword")
                .build();

        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(user);


        // Act
        boolean passwordChanged;
        try {
            passwordChanged = userRestService.changePassword(username, inboundUserChangePasswordDto, httpSession);
        } catch (UserNotFoundException | InvalidSessionException | InvalidPasswordException e) {
            throw new RuntimeException(e);
        }

        // Assert
        Assertions.assertTrue(passwordChanged);
    }

    @Test
    void changePasswordWithInvalidPassword() {
        InboundUserChangePasswordDto inboundUserChangePasswordDto = InboundUserChangePasswordDto.builder()
                .oldPassword("password")
                .newPassword("newPassword")
                .controlNewPassword("newerPassword")
                .build();

        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(user);


        Assertions.assertThrows(InvalidPasswordException.class,
                () -> userRestService.changePassword(username, inboundUserChangePasswordDto, httpSession));
    }


    @Test
    void removeUserByUsername() {
        // Arrange
        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(user);

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> userRestService.removeUserByUsername(username, password, httpSession));


    }

}