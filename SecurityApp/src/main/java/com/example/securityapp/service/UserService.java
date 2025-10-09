package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.UserRepositoryInterface;
import com.example.securityapp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepositoryInterface userRepositoryInterface;

    @Autowired
    public UserService(UserRepositoryInterface userRepositoryInterface){
        this.userRepositoryInterface=userRepositoryInterface;
    }

    public User findByEmail(String email){
        return userRepositoryInterface.findUserByEmail(email);
    }

    //public User findByUsername(String username){
        //return userRepositoryInterface.findUserByUsername(username);
    //}

    public User findById(Integer id){
        return userRepositoryInterface.findUserById(id);
    }

    public User save(User user){
        return userRepositoryInterface.save(user);
    }

    public boolean isEmailExist(String email){return userRepositoryInterface.existsByEmail(email);}

}
