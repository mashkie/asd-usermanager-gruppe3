package at.ac.fhcampuswien.asd.rest.service;

import at.ac.fhcampuswien.asd.entity.models.User;
import at.ac.fhcampuswien.asd.entity.services.UserEntityService;
import at.ac.fhcampuswien.asd.exceptions.*;
import at.ac.fhcampuswien.asd.helper.Hashing;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.model.OutboundUserRegistrationDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRestService {

    @NonNull UserEntityService userEntityService;


    public boolean comparePassword(User user, String enteredPassword) {

        byte[] hash = Hashing.generateHash(enteredPassword, user.getSalt());
        return Arrays.equals(hash, user.getPassword());

    }

    public boolean login(String username, String password, UUID sessionId) throws AuthenticationException, UserLockedException, InvalidPasswordException {

        User user = userEntityService.getUserByUsername(username);

        checkUserExistence(user);
        checkLockedStatus(password, user);
        user = checkPassword(password, user);
        resetLock(user);
        resetFailedLoginCounter(user);
        userEntityService.setSessionId(user, sessionId);

        return true;
    }

    private void resetFailedLoginCounter(User user) {
        if (user.getFailedLoginCounter() != 0) {
            userEntityService.resetFailedCounter(user);
        }
    }

    private void resetLock(User user) {
        if (user.getLockedUntil() != null) {
            userEntityService.resetLock(user);
        }
    }

    private void checkUserExistence(User user) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("This user does not exist!");
        }
    }

    private User checkPassword(String password, User user) throws InvalidPasswordException {
        if (!comparePassword(user, password)) {
            user = userEntityService.incrementFailedLoginCount(user);
            if (user.getFailedLoginCounter() >= 4) {
                userEntityService.lockUser(user);
            }
            throw new InvalidPasswordException("The password is not correct!");
        }
        return user;
    }

    private void checkLockedStatus(String password, User user) throws UserLockedException {
        if (comparePassword(user, password) && user.getLockedUntil() != null && user.getLockedUntil() > new Date().getTime()) {
            throw new UserLockedException("The user is locked, login will be possible at " + new Date(user.getLockedUntil()).toString());
        }
    }

    public void logout(String username, UUID session) throws InvalidSessionException, AuthenticationException {

        User user = userEntityService.getUserByUsername(username);
        checkUserExistence(user);

        if (user.getSession() != session) {
            throw new InvalidSessionException("The session for the user is invalid.");
        } else {
            userEntityService.removeSessionId(user);
        }

    }

    public OutboundUserRegistrationDto createUser(InboundUserRegistrationDto inboundUserRegistrationDto) throws UserAlreadyExistsException {
        return userEntityService.addUser(inboundUserRegistrationDto);
    }


}
