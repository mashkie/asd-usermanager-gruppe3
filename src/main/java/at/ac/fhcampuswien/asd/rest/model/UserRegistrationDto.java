package at.ac.fhcampuswien.asd.rest.model;

import lombok.Data;

@Data
public class UserRegistrationDto {

    String username;
    String firstName;
    String lastName;
    String password;
}
