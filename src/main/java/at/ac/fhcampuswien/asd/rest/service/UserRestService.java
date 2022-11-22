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


    /**
     * Compares the password specified by the user with the encrypted version in the database.
     *
     * @param user            The user for which the password is compared.
     * @param enteredPassword The password entered.
     * @return Returns true if the password matches, false otherwise.
     */
    public boolean comparePassword(User user, String enteredPassword) {

        byte[] hash = Hashing.generateHash(enteredPassword, user.getSalt());
        return Arrays.equals(hash, user.getPassword());

    }

    /**
     * Logs in the user.
     *
     * @param username  The username used ot identify the user.
     * @param password  The password for the specified user.
     * @param sessionId The sessionId stored in the HTTPSession.
     * @throws UserNotFoundException    In case the user does not exist.
     * @throws UserLockedException      In case the user is locked.
     * @throws InvalidPasswordException In case the specified password is incorrect.
     */
    public void login(String username, String password, UUID sessionId) throws UserNotFoundException, UserLockedException, InvalidPasswordException {

        User user = checkUserExistence(username);
        checkLockedStatus(user);
        checkPassword(password, user);
        resetLock(user);
        resetFailedLoginCounter(user);
        userEntityService.setSessionId(user, sessionId);

    }

    /**
     * Resets the failed login counter.
     *
     * @param user The user for which to reset the counter.
     */
    private void resetFailedLoginCounter(User user) {
        if (user.getFailedLoginCounter() != 0) {
            userEntityService.resetFailedCounter(user);
        }
    }

    /**
     * Resets the lockout timer.
     *
     * @param user The user for which to reset the lockout.
     */
    private void resetLock(User user) {
        if (user.getLockedUntil() != null) {
            userEntityService.resetLock(user);
        }
    }

    /**
     * Checks the users' existence.
     *
     * @param username The username of the user to check.
     * @return Returns the User.
     * @throws UserNotFoundException In case the user is not found in the database.
     */
    private User checkUserExistence(String username) throws UserNotFoundException {
        User user = userEntityService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("This user does not exist!");
        }
        return user;
    }

    /**
     * Validates the password specified by the user.
     *
     * @param password The password specified by the user.
     * @param user     The user for which to validate the password for.
     * @throws InvalidPasswordException In case the password is invalid.
     */
    private void checkPassword(String password, User user) throws InvalidPasswordException {
        if (!comparePassword(user, password)) {
            user = userEntityService.incrementFailedLoginCount(user);
            if (user.getFailedLoginCounter() >= 4) {
                userEntityService.setLockTime(user);
            }
            throw new InvalidPasswordException("The password is not correct!");
        }
    }

    /**
     * Checks the locked status.
     *
     * @param user The user for which to check the locked status.
     * @throws UserLockedException In case the user is locked.
     */
    private void checkLockedStatus(User user) throws UserLockedException {
        if (user.getLockedUntil() != null && user.getLockedUntil() > new Date().getTime()) {
            throw new UserLockedException("The user is locked, login will be possible at " + new Date(user.getLockedUntil()));
        }
    }

    /**
     * Ends the users' session.
     *
     * @param username The username of the users for which to end the session.
     * @param session  The session id of the session to end.
     * @throws InvalidSessionException In case the session does not match the users session.
     * @throws UserNotFoundException   In case the user does not exist.
     */
    public void logout(String username, UUID session) throws InvalidSessionException, UserNotFoundException {

        User user = checkUserExistence(username);
        if (user.getSession() != session) {
            throw new InvalidSessionException("The session for the user is invalid.");
        } else {
            userEntityService.removeSessionId(user);
        }

    }

    /**
     * Creates the user in the database.
     *
     * @param inboundUserRegistrationDto Specifies information required for the user creation.
     * @return Returns an outbound representation of the user.
     * @throws UserAlreadyExistsException In case the user already exists.
     */

    public OutboundUserRegistrationDto createUser(InboundUserRegistrationDto inboundUserRegistrationDto) throws UserAlreadyExistsException {
        return userEntityService.addUser(inboundUserRegistrationDto);
    }


}