package dto;

import domain.Role;
import domain.User;

public class UserDTO {
    public int id;
    public String name;
    public String surname;
    public Role role;
    public String password;
    public String email;

    public UserDTO(){}

    public UserDTO(User user){
        this(user.getId(),user.getName(),user.getSurname(),user.getRole(),user.getPassword(),user.getEmail());
    }

    public UserDTO(int id,String name,String surname,Role role,String password,String email){
        this.id=id;
        this.name=name;
        this.surname=surname;
        this.role=role;
        this.password=password;
        this.email=email;
    }

}
