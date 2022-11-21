package at.ac.fhcampuswien.asd.rest.service;

import at.ac.fhcampuswien.asd.entity.models.User;
import at.ac.fhcampuswien.asd.entity.services.UserEntityService;
import at.ac.fhcampuswien.asd.exceptions.AuthenticationException;
import at.ac.fhcampuswien.asd.exceptions.UserAlreadyExistsException;
import at.ac.fhcampuswien.asd.helper.Hashing;
import at.ac.fhcampuswien.asd.rest.model.UserRegistrationDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserRestService {

    @NonNull
    UserEntityService userEntityService;

    public boolean comparePassword(User user, String enteredPassword) {

        byte[] hash = Hashing.generateHash(enteredPassword, user.getSalt());
        return Arrays.equals(hash, user.getPassword());

    }

    public boolean login(String username, String password) throws AuthenticationException {

        User user = userEntityService.getUserByUsername(username);
        if (user == null || !comparePassword(user, password)) {
            throw new AuthenticationException("The authorization failed!");
        }
        return true;
    }


    public User createUser(UserRegistrationDto userRegistrationDto) throws UserAlreadyExistsException {
        return userEntityService.addUser(userRegistrationDto);
    }


}
