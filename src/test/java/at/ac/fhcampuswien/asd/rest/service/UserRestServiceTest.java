package at.ac.fhcampuswien.asd.rest.service;

import at.ac.fhcampuswien.asd.entity.models.User;
import at.ac.fhcampuswien.asd.entity.services.UserEntityService;
import at.ac.fhcampuswien.asd.exceptions.AuthenticationException;
import at.ac.fhcampuswien.asd.exceptions.InvalidSessionException;
import at.ac.fhcampuswien.asd.exceptions.UserLockedException;
import at.ac.fhcampuswien.asd.exceptions.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@SpringBootTest
class UserRestServiceTest {

    static UUID sessionId;
    private final long sessionValidUntil = new Date().getTime() + 1000 * 60 * 60;
    String password = "password";
    String username = "username";
    String firstname = "thomas";
    String lastname = "scheibelhofer";
    @Autowired
    @InjectMocks
    UserRestService userRestService;
    @Mock
    UserEntityService userEntityService;

    User dbUser;


    @BeforeAll
    public static void setUpUUID() {
        sessionId = UUID.randomUUID();
    }

    @BeforeEach
    void setUp() {


        // Create the user in the DB
        dbUser = User.builder()
                .username(username)
                .firstName(firstname)
                .lastName(lastname)
                .session(sessionId)
                .sessionValidUntil(sessionValidUntil)
                .build();
        dbUser.setPassword(password);

    }

    @AfterEach
    void tearDown() {

    }


    @Test
    void comparePassword() {
        // Arrange
        User user = User.builder()
                .username(username)
                .firstName(firstname)
                .lastName(lastname)
                .session(sessionId)
                .build();
        user.setPassword(password);

        // Act
        boolean passwordsAreEqual = userRestService.comparePassword(user, password);

        // Assert
        Assertions.assertTrue(passwordsAreEqual);

    }

    @Test
    void sessionValid() {
        // Arrange
        User user = User.builder()
                .username(username)
                .firstName(firstname)
                .lastName(lastname)
                .session(sessionId)
                .build();
        user.setPassword(password);
        user.setSessionValidUntil(new Date().getTime() + 1000 * 60 * 60);

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
        // Arrange
        User user = User.builder()
                .username(username)
                .firstName(firstname)
                .lastName(lastname)
                .session(sessionId)
                .build();
        user.setPassword(password);
        user.setSessionValidUntil(new Date().getTime());

/*        // Manipulate the user in the DB
        User dbUser = userRepository.findByUsername(username);
        dbUser.setSessionValidUntil(new Date().getTime() - 1000 * 60);
        userRepository.save(dbUser);*/

        // Act && Assert
        Assertions.assertThrows(InvalidSessionException.class, () -> userRestService.logoutUserOnInvalidSession(user));

    }

    @Test
    void login() {

        // Arrange
        boolean userIsLoggedIn;

        // Act

        try {
            userIsLoggedIn = userRestService.login(username, password, sessionId);
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        } catch (UserLockedException e) {
            throw new RuntimeException(e);
        }

        // Assert session validity has been renewed
        Assertions.assertTrue(userIsLoggedIn);

    }

    @Test
    void logout() {
        // Arrange
        boolean userIsLoggedOut;
        Mockito.when(userEntityService.getUserByUsername(username))
                .thenReturn(dbUser);

        // Act
        try {
            userIsLoggedOut = userRestService.logout(username, sessionId);
        } catch (InvalidSessionException | UserNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Assert


    }

    @Test
    void createUser() {

        //Arrange
/*                inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .password(password)
                .firstName(firstname)
                .lastName(lastname)
                .build();

        // Create the user in the DB
        userRestService.createUser(inboundUserRegistrationDto);


        // Act
        boolean createUser = userRestService.createUser(inBound);

        // Assert
        Assertions.assertTrue(createUser);*/

    }

    @Test
    void changePassword() {
        // Arrange
//        User user = User.builder()
//                .username(username)
//                .firstName(firstname)
//                .lastName(lastname)
//                .build();
//        user.setPassword(password);
//        string

        // Act

        // Assert

        //Assertions.assertTrue();

    }

    @Test
    void removeUserByUsername() {
    }
}