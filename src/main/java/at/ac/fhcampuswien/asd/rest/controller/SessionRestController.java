package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.exceptions.AuthenticationException;
import at.ac.fhcampuswien.asd.exceptions.InvalidPasswordException;
import at.ac.fhcampuswien.asd.exceptions.InvalidSessionException;
import at.ac.fhcampuswien.asd.exceptions.UserLockedException;
import at.ac.fhcampuswien.asd.rest.model.LoginForm;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("users")
public class SessionRestController {

    UserRestService userService;

    final String sessionIdName = "X-SESSION-ID";


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginForm loginForm, HttpSession session) throws AuthenticationException, UserLockedException, InvalidPasswordException {

        UUID sessionID = UUID.randomUUID();
        session.setAttribute(sessionIdName, UUID.randomUUID());
        userService.login(loginForm.getUsername(), loginForm.getPassword(), sessionID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logout/{username}")
    public ResponseEntity<Object> logout(@PathVariable String username, HttpSession session) throws InvalidSessionException, AuthenticationException {

        userService.logout(username, (UUID) session.getAttribute(sessionIdName));
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
