package service;

import RepositoryInterfaces.UserRepositoryInterface;
import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepositoryInterface userRepositoryInterface;

    @Autowired
    public UserService(UserRepositoryInterface userRepositoryInterface){
        this.userRepositoryInterface=userRepositoryInterface;
    }


}
