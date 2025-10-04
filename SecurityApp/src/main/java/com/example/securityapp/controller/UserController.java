package com.example.securityapp.controller;

import com.example.securityapp.RepositoryInterfaces.VerificationTokenRepositoryInterface;
import com.example.securityapp.auth.AuthenticationResponse;
import com.example.securityapp.auth.JwtUtil;
import com.example.securityapp.auth.SessionInfo;
import com.example.securityapp.auth.SessionRegistry;
import com.example.securityapp.domain.Role;
import com.example.securityapp.domain.User;
import com.example.securityapp.domain.VerificationToken;
import com.example.securityapp.dto.LogInRequest;
import com.example.securityapp.dto.UserDTO;
import com.example.securityapp.mapper.UserDTOMapper;
import com.example.securityapp.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.securityapp.service.EmailService;
import com.example.securityapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private VerificationTokenService verificationTokenService;
    private VerificationTokenRepositoryInterface verificationTokenRepositoryInterface;

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
        savedUser.setRole(Role.BASIC);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(userDTO.password);
        savedUser.setPassword(hash);
        savedUser.setVerified(false);

        try {
            userService.save(savedUser);

            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(savedUser);
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
            verificationToken.setUsed(false);

            verificationTokenService.save(verificationToken);

            String activationLink = "http://localhost:8080/api/users/verify?token=" + token;
            emailService.sendVerificationEmail(userDTO, activationLink);
            return new ResponseEntity<>("User successefully created", HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Cannot send verification email",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token){
        VerificationToken verificationToken = verificationTokenService.findByToken(token);

        if (verificationToken == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token");
        }

        if (verificationToken.isUsed()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token already used");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired");
        }

        User user = verificationToken.getUser();

    if(user!=null){
        if (user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already verified");
        }

        if(verificationToken.getExpiryDate().compareTo(LocalDateTime.now()) < 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Link expired");
        }

        user.setVerified(true);
        userService.save(user);

        verificationToken.setUsed(true);
        verificationTokenService.save(verificationToken);
        return new ResponseEntity<>("Success verification user found",HttpStatus.OK);
    }
     return new ResponseEntity<>("Unsuccessful verification user is null",HttpStatus.NOT_FOUND);
    }

    @PostMapping(value="/login")
    public ResponseEntity<?> login( @RequestBody LogInRequest logInRequest,HttpServletRequest request){

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

            String sessionId = UUID.randomUUID().toString();
            String token = jwtUtil.generateToken(user.getId(), user.getRole(),sessionId);

            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }

            String userAgent = request.getHeader("User-Agent");
            sessionRegistry.registerSession(sessionId, ipAddress, userAgent, user.getId());

            AuthenticationResponse response = new AuthenticationResponse(token);

            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value="/recovery")
    public ResponseEntity<?> recovery( @RequestBody LogInRequest logInRequest){

        System.out.println(logInRequest.getEmail());
        User user = userService.findByEmail(logInRequest.getEmail());

        if (user == null) {
            return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_FOUND);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(logInRequest.getPassword());
        user.setPassword(hash);
        user.setVerified(false);

        UserDTO userDTO = userDTOMapper.fromUserToDTO(user);
        userDTO.email=user.getEmail();

        try {
            userService.save(user);

            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
            verificationToken.setUsed(false);

            verificationTokenService.save(verificationToken);

            String activationLink = "http://localhost:8080/api/users/verify?token=" + token;
            emailService.sendPasswordRecoveryEmail(userDTO, activationLink);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Successfully changed password"));
        } catch (Exception e) {
            return new ResponseEntity<>("Cannot change password",HttpStatus.NOT_ACCEPTABLE);
        }
    }

    //dobavljanje sesije
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionInfo>> getActiveSessions(@RequestParam int userId) {
        return ResponseEntity.ok(sessionRegistry.getUserSessions(userId));
    }

    //gasenje sesije
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> revokeSession(@PathVariable String sessionId) {
        sessionRegistry.removeSession(sessionId);
        return ResponseEntity.ok().build();
    }


}
