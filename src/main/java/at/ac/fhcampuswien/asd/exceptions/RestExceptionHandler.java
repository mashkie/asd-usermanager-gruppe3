package at.ac.fhcampuswien.asd.exceptions;

import at.ac.fhcampuswien.asd.rest.model.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This class catches exceptions from REST controllers and handles them as specified in the individual methods.
 */
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({InvalidPasswordException.class, UserLockedException.class, InvalidSessionException.class})
    public ResponseEntity<?> handleInvalidPassword(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseMessage(e.getMessage()));
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleDuplicateUsername(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseMessage(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleMissingUser(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleInvalidAuthentication(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseMessage(e.getMessage()));
    }
}
