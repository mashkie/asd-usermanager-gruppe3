package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.exceptions.AuthenticationException;
import at.ac.fhcampuswien.asd.rest.model.LoginForm;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class LoginRestController {

    UserRestService userService;


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginForm loginForm) {

        // todo: session management

        try {
            userService.login(loginForm.getUsername(), loginForm.getPassword());
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
