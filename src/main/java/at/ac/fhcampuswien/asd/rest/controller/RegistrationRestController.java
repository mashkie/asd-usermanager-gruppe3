package at.ac.fhcampuswien.asd.rest.controller;

import at.ac.fhcampuswien.asd.exceptions.UserAlreadyExistsException;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.model.OutboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.service.UserRestService;
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
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationRestController {

    UserRestService userRestService;

    @PostMapping("/register")
    public ResponseEntity<OutboundUserRegistrationDto> register(@RequestBody InboundUserRegistrationDto inboundUserRegistrationDto) throws UserAlreadyExistsException {

        OutboundUserRegistrationDto outboundUserRegistrationDto = userRestService.createUser(inboundUserRegistrationDto);
        return new ResponseEntity<>(outboundUserRegistrationDto, HttpStatus.CREATED);

    }
}

