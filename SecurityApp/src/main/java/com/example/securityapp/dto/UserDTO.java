package com.example.securityapp.dto;

import com.example.securityapp.domain.Role;
import com.example.securityapp.domain.User;

public class UserDTO {
    public int id;
    public String name;
    public String surname;
    public String email;
    public String password;
    public Role role;
    public String organization;
    public boolean isVerified;

    public UserDTO(){}

    public UserDTO(User user){
        this(user.getId(),user.getName(),user.getSurname(),user.getEmail(),user.getPassword(),user.getRole(),user.getOrganization(),user.isVerified());
    }

    public UserDTO(int id,String name,String surname,String email,String password,Role role,String organization,boolean isVerified){
        this.id=id;
        this.name=name;
        this.surname=surname;
        this.email=email;
        this.password=password;
        this.role=role;
        this.organization=organization;
        this.isVerified=isVerified;
    }

}
