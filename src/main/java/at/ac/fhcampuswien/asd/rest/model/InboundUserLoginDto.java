package at.ac.fhcampuswien.asd.rest.model;

import lombok.Data;

@Data
public class InboundUserLoginDto {

    private String username;
    private String password;

}
