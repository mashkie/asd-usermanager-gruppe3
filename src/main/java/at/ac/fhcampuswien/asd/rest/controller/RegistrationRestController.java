package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.exceptions.UserAlreadyExistsException;
import at.ac.fhcampuswien.asd.rest.model.UserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class RegistrationRestController {

    UserRestService userRestService;

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            userRestService.createUser(userRegistrationDto);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

