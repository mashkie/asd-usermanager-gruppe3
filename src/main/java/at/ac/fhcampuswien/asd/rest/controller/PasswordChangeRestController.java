package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.exceptions.InvalidPasswordException;
import at.ac.fhcampuswien.asd.exceptions.InvalidSessionException;
import at.ac.fhcampuswien.asd.exceptions.UserNotFoundException;
import at.ac.fhcampuswien.asd.rest.model.InboundUserChangePasswordDto;
import at.ac.fhcampuswien.asd.rest.model.ResponseMessage;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class PasswordChangeRestController {

    UserRestService userService;

    final static String sessionIdName = "X-SESSION-ID";

    /**
     * @param username Specifies the user whose password should be changed
     * @param session  Specifies the session of the user.
     * @return Returns OK.
     * @throws InvalidSessionException In case the session does not exist for the specified user.
     * @throws UserNotFoundException   In case the user does not exist.
     */

    @PutMapping("/{username}/password")
    @Operation(
            summary = "Change password of a user.",
            tags = {"Users"},
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Invalid session", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Invalid password", responseCode = "401", content = @Content),
            }
    )
    public ResponseEntity<Object> changePassword(@PathVariable String username, @RequestBody InboundUserChangePasswordDto inboundUserChangePasswordDto, HttpSession session) throws InvalidSessionException, UserNotFoundException, InvalidPasswordException {
        userService.changePassword(username, inboundUserChangePasswordDto, (UUID) session.getAttribute(sessionIdName));
        return new ResponseEntity<>(new ResponseMessage("Password was changed"), HttpStatus.OK);
    }
}
