package at.ac.fhcampuswien.asd.rest.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class DeleteUserRequest {
    @NotBlank
    @NotEmpty
    private String currentPassword;
}
