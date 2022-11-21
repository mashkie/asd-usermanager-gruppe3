package at.ac.fhcampuswien.asd.entity.services;

import at.ac.fhcampuswien.asd.entity.models.User;
import at.ac.fhcampuswien.asd.entity.repository.UserRepository;
import at.ac.fhcampuswien.asd.exceptions.UserAlreadyExistsException;
import at.ac.fhcampuswien.asd.rest.mapper.UserMapper;
import at.ac.fhcampuswien.asd.rest.model.UserRegistrationDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserEntityService {

    UserRepository userRepository;

    UserMapper userMapper;

    public boolean checkUserExistence(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public User addUser(UserRegistrationDto userDto) throws UserAlreadyExistsException {
        if (checkUserExistence(userDto.getUsername())) {
            throw new UserAlreadyExistsException("The username is already taken!");
        }
        User user = userMapper.toModel(userDto);
        userRepository.save(user);
        return user;
    }

}
