package at.ac.fhcampuswien.asd.rest.model;

import lombok.Data;

@Data
public class InboundUserChangePasswordDto {

    private String oldPassword;
    private String newPassword;
    private String controlNewPassword;
}
