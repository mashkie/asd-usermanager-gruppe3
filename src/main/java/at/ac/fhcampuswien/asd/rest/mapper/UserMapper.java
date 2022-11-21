package at.ac.fhcampuswien.asd.rest.mapper;

import at.ac.fhcampuswien.asd.entity.models.User;
import at.ac.fhcampuswien.asd.rest.model.UserRegistrationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class UserMapper {

    public User toModel(UserRegistrationDto userRegistrationDto) {

        User user = User.builder()
                .username(userRegistrationDto.getUsername())
                .firstName(userRegistrationDto.getFirstName())
                .lastName(userRegistrationDto.getLastName())
                .build();
        user.setPassword(userRegistrationDto.getPassword());
        return user;

    }

}
