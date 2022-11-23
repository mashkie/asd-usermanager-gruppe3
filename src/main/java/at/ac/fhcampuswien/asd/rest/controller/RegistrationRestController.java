package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.exceptions.UserAlreadyExistsException;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.model.OutboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("users")
@Tag(name = "Users", description = "Endpoints for managing users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationRestController {

    UserRestService userRestService;

    /**
     * Registers the user in the database.
     *
     * @param inboundUserRegistrationDto Specifies information to create the user account.
     * @return Returns HTTP Created.
     * @throws UserAlreadyExistsException In case the username is already taken.
     */
    @Operation(
            summary = "Creates a user in the database.",
            tags = {"Users"},
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OutboundUserRegistrationDto.class))
                    ),
                    @ApiResponse(description = "User already Exists", responseCode = "409", content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<OutboundUserRegistrationDto> register(@RequestBody InboundUserRegistrationDto inboundUserRegistrationDto) throws UserAlreadyExistsException {

        OutboundUserRegistrationDto outboundUserRegistrationDto = userRestService.createUser(inboundUserRegistrationDto);
        return new ResponseEntity<>(outboundUserRegistrationDto, HttpStatus.CREATED);

    }
}

