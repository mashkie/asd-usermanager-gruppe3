package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.exceptions.InvalidPasswordException;
import at.ac.fhcampuswien.asd.exceptions.InvalidSessionException;
import at.ac.fhcampuswien.asd.exceptions.UserNotFoundException;
import at.ac.fhcampuswien.asd.rest.model.DeleteUserRequest;
import at.ac.fhcampuswien.asd.rest.model.OutboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.model.ResponseMessage;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeletionRestController {

    UserRestService userRestService;

    final static String sessionIdName = "X-SESSION-ID";

    /**
     * Registers the user in the database.
     *
     * @return Returns HTTP Created.
     * @throws UserNotFoundException In case the username don't exist.
     */
    @Operation(
            summary = "Delete a user in the database.",
            tags = {"Users"},
            responses = {
                    @ApiResponse(
                            description = "Removed",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OutboundUserRegistrationDto.class))
                    ),
                    @ApiResponse(description = "User don`t exists", responseCode = "404", content = @Content)
            }
    )
    @DeleteMapping("/users/{username}")
    public ResponseEntity<String> remove(@PathVariable String username,
                                         @RequestBody DeleteUserRequest deleteUserRequest,
                                         HttpSession session) throws UserNotFoundException, InvalidSessionException, InvalidPasswordException {
        userRestService.removeUserByUsername(username, deleteUserRequest.getCurrentPassword(), session);
        session.removeAttribute(sessionIdName);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
