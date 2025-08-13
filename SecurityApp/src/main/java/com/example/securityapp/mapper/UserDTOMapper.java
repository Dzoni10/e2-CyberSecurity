package com.example.securityapp.mapper;

import com.example.securityapp.domain.User;
import com.example.securityapp.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public UserDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User fromDTOtoUser(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }

    public UserDTO fromUserToDTO(User user){
        return modelMapper.map(user,UserDTO.class);
    }

}
