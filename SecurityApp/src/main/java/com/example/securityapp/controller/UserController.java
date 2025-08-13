package com.example.securityapp.controller;

import com.example.securityapp.auth.AuthenticationResponse;
import com.example.securityapp.auth.JwtUtil;
import com.example.securityapp.domain.Role;
import com.example.securityapp.domain.User;
import com.example.securityapp.dto.LogInRequest;
import com.example.securityapp.dto.UserDTO;
import com.example.securityapp.mapper.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.securityapp.service.EmailService;
import com.example.securityapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserDTOMapper userDTOMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(description = "Create new user", method = "POST")
    @PostMapping(value="/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO) {

        if(userService.isEmailExist(userDTO.email))
        {
            return new ResponseEntity<>("Email exits", HttpStatus.BAD_REQUEST);
        }

        User savedUser = new User();

        savedUser.setEmail(userDTO.email);
        savedUser.setName(userDTO.name);
        savedUser.setSurname(userDTO.surname);
        savedUser.setOrganization(userDTO.organization);
        savedUser.setRole(Role.ROLE_BASIC);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(userDTO.password);
        savedUser.setPassword(hash);
        savedUser.setVerified(false);

        try {
            userService.save(savedUser);

            String activationLink = "http://localhost:8080/api/users/verify?email=" + savedUser.getEmail();
            emailService.sendVerificationEmail(userDTO, activationLink);
            return new ResponseEntity<>("User successefully created", HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Cannot send verification email",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("email") String email){
    User user = userService.findByEmail(email);

    if(user!=null){
        user.setVerified(true);
        userService.save(user);
        return new ResponseEntity<>("Success verification",HttpStatus.OK);
    }
     return new ResponseEntity<>("Unsuccessful verification",HttpStatus.NOT_FOUND);
    }

    @PostMapping(value="/login")
    public ResponseEntity<?> login( @RequestBody LogInRequest logInRequest){


        User user = userService.findByEmail(logInRequest.getEmail());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(user==null ){
            return new ResponseEntity<>("Unsuccessful login - user doesn't exist",HttpStatus.NOT_FOUND);
        }

        if (!encoder.matches(logInRequest.getPassword(), user.getPassword())) {
            return new ResponseEntity<>("Unsuccessful login - wrong password", HttpStatus.UNAUTHORIZED);
        }

        if (!user.isVerified()) {
            return new ResponseEntity<>("Unsuccessful login - mail not verified", HttpStatus.NOT_ACCEPTABLE);
        }

        UserDTO userDTO = new UserDTO();

            String token = jwtUtil.generateToken(user.getId(), user.getRole());

            AuthenticationResponse response = new AuthenticationResponse(token);

            return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
