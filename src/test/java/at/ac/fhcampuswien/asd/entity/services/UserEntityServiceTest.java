package at.ac.fhcampuswien.asd.entity.services;

import at.ac.fhcampuswien.asd.entity.models.User;
import at.ac.fhcampuswien.asd.entity.repository.UserRepository;
import at.ac.fhcampuswien.asd.exceptions.UserAlreadyExistsException;
import at.ac.fhcampuswien.asd.helper.Hashing;
import at.ac.fhcampuswien.asd.rest.mapper.UserMapper;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.model.OutboundUserRegistrationDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@SpringBootTest
class UserEntityServiceTest {

    static UUID sessionId;
    private final long sessionValidUntil = new Date().getTime() + Duration.ofHours(1)
            .toMillis();
    String password = "password";
    String username = "username";
    String firstname = "thomas";
    String lastname = "scheibelhofer";
    @Mock
    UserRepository userRepository;
    @Autowired
    UserMapper actualUserMapper;
    @Mock
    UserMapper mockUserMapper;
    @InjectMocks
    UserEntityService userEntityService;


    @Test
    void checkUserExistence_true() {
        // Arrange
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);

        // Act
        boolean userExists = userEntityService.checkUserExistence(username);

        // Assert
        Assertions.assertTrue(userExists);

    }

    @Test
    void checkUserExistence_false() {
        // Arrange
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(false);

        // Act
        boolean userExists = userEntityService.checkUserExistence(username);

        // Assert
        Assertions.assertFalse(userExists);

    }

    @Test
    void getUserByUsername_correct_user() {
        // Arrange
        User user = getUser();
        Mockito.when(userRepository.findByUsername(username)).thenReturn(user);

        // Act
        User userFound = userEntityService.getUserByUsername(username);

        // Assert
        Assertions.assertEquals(user, userFound);

    }

    @Test
    void getUserByUsername_incorrect_user() {
        // Arrange
        User correctUser = getUser();
        User duplicateUser = getUser();
        Mockito.when(userRepository.findByUsername(username)).thenReturn(duplicateUser);

        // Act
        User userFound = userEntityService.getUserByUsername(username);

        // Assert
        Assertions.assertNotEquals(correctUser, userFound);

    }

    @Test
    void addUser_success() throws UserAlreadyExistsException {
        // Arrange
        InboundUserRegistrationDto inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .password(password)
                .firstName(firstname)
                .lastName(lastname)
                .build();
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(false);
        User user = actualUserMapper.inboundToModel(inboundUserRegistrationDto);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(mockUserMapper.inboundToModel(inboundUserRegistrationDto)).thenReturn(user);
        OutboundUserRegistrationDto mockedOutboundUserRegistrationDTO = actualUserMapper.modelToOutboundDto(user);
        Mockito.when(mockUserMapper.modelToOutboundDto(user)).thenReturn(mockedOutboundUserRegistrationDTO);

        // Act
        OutboundUserRegistrationDto outboundUserRegistrationDto = userEntityService.addUser(inboundUserRegistrationDto);

        // Assert
        Assertions.assertEquals(outboundUserRegistrationDto.getUsername(), inboundUserRegistrationDto.getUsername());

    }

    @Test
    void addUser_duplicate() {
        // Arrange
        InboundUserRegistrationDto inboundUserRegistrationDto = InboundUserRegistrationDto.builder()
                .username(username)
                .password(password)
                .firstName(firstname)
                .lastName(lastname)
                .build();
        Mockito.when(userEntityService.checkUserExistence(username)).thenReturn(true);

        // Act && Assert
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userEntityService.addUser(inboundUserRegistrationDto));
    }


    @Test
    void setLockTime_success() {
        // Arrange
        User user = getUser();
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userEntityService.setLockTime(user);

        // Assert
        Assertions.assertTrue(savedUser.getLockedUntil() > new Date().getTime());

    }

    @Test
    void resetLock_success() {
        // Arrange
        User user = getUser();
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userEntityService.resetLock(user);

        // Assert
        Assertions.assertNull(savedUser.getLockedUntil());

    }

    @Test
    void setSessionId() {
        // Arrange
        User user = getUser();
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userEntityService.setSessionId(user, sessionId);

        // Assert
        Assertions.assertEquals(sessionId, savedUser.getSession());

    }

    @Test
    void removeSessionId() {
        // Arrange
        User user = getUser();
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userEntityService.removeSessionId(user);

        // Assert
        Assertions.assertNull(savedUser.getSession());

    }

    @Test
    void setPassword() {
        // Arrange
        User user = getUser();
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userEntityService.setPassword(user, password);

        // Assert
        Assertions.assertArrayEquals(Hashing.generateHash(password, user.getSalt()), savedUser.getPassword());

    }

    private User getUser() {
        return User.builder()
                .username(username)
                .firstName(firstname)
                .lastName(lastname)
                .session(sessionId)
                .sessionValidUntil(sessionValidUntil)
                .build();
    }
}