package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.exceptions.InvalidPasswordException;
import at.ac.fhcampuswien.asd.exceptions.InvalidSessionException;
import at.ac.fhcampuswien.asd.exceptions.UserLockedException;
import at.ac.fhcampuswien.asd.exceptions.UserNotFoundException;
import at.ac.fhcampuswien.asd.rest.model.InboundUserLoginDto;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Endpoints for managing users")
public class SessionRestController {

    UserRestService userService;

    final String sessionIdName = "X-SESSION-ID";

    /**
     * @param inboundUserLoginDto Specified username and password.
     * @param session             HTTP Session object
     * @return Returns OK.
     * @throws UserNotFoundException    In case the user does not exist.
     * @throws UserLockedException      In case the user is locked.
     * @throws InvalidPasswordException In case the specified password is invalid.
     */

    @PostMapping("/login")
    @Operation(
            summary = "Login a user.",
            tags = {"Users"},
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "User locked", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Invalid password", responseCode = "401", content = @Content)
            }
    )
    public ResponseEntity<Object> login(@RequestBody InboundUserLoginDto inboundUserLoginDto, HttpSession session) throws UserNotFoundException, UserLockedException, InvalidPasswordException {

        UUID sessionID = UUID.randomUUID();
        session.setAttribute(sessionIdName, sessionID);
        userService.login(inboundUserLoginDto.getUsername(), inboundUserLoginDto.getPassword(), sessionID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param username Specifies the user to be logged out.
     * @param session  Specifies the session to close.
     * @return Returns OK.
     * @throws InvalidSessionException In case the session does not exist for the specified user.
     * @throws UserNotFoundException   In case the user does not exist.
     */

    @PostMapping("/logout/{username}")
    @Operation(
            summary = "Logs out a user.",
            tags = {"Users"},
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Invalid session", responseCode = "401", content = @Content)
            }
    )
    public ResponseEntity<Object> logout(@PathVariable String username, HttpSession session) throws InvalidSessionException, UserNotFoundException {

        userService.logout(username, (UUID) session.getAttribute(sessionIdName));
        return new ResponseEntity<>(HttpStatus.OK);
    }


}