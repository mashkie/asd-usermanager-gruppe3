package at.ac.fhcampuswien.asd.rest.mapper;

import at.ac.fhcampuswien.asd.entity.models.User;
import at.ac.fhcampuswien.asd.rest.model.InboundUserRegistrationDto;
import at.ac.fhcampuswien.asd.rest.model.OutboundUserRegistrationDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class UserMapper {

    public User inboundToModel(InboundUserRegistrationDto inboundUserRegistrationDto) {

        User user = User.builder()
                .username(inboundUserRegistrationDto.getUsername())
                .firstName(inboundUserRegistrationDto.getFirstName())
                .lastName(inboundUserRegistrationDto.getLastName())
                .build();
        user.setPassword(inboundUserRegistrationDto.getPassword());
        return user;

    }

    public OutboundUserRegistrationDto modelToOutboundDto(User user) {

        return OutboundUserRegistrationDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

}
