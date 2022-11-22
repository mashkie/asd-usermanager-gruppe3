package at.ac.fhcampuswien.asd.exceptions;

import at.ac.fhcampuswien.asd.rest.model.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({InvalidPasswordException.class, AuthenticationException.class, UserLockedException.class, InvalidSessionException.class})
    public ResponseEntity<?> handleInvalidPassword(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseMessage(e.getMessage()));
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleDuplicateUsername(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseMessage(e.getMessage()));
    }


}
