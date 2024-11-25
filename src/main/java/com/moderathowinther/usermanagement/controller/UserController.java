package com.moderathowinther.usermanagement.controller;

import jakarta.validation.Valid;
import lombok.Data;
import com.moderathowinther.usermanagement.dto.UserDto;
import com.moderathowinther.usermanagement.exception.UserNotFoundException;
import com.moderathowinther.usermanagement.repository.UserRepository;
import com.moderathowinther.usermanagement.service.UserService;
import com.moderathowinther.usermanagement.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@RestController
@Validated
@RequestMapping(path="/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody UserDto userDto) {
        User u = userService.registerNewUser(userDto);

        List<GrantedAuthority> authorities = userDto.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        Map<String, String> loginData = new HashMap<>();
        loginData.putIfAbsent("password", userDto.getPassword());
        loginData.putIfAbsent("email", userDto.getEmail());

        login(loginData);

        return ResponseEntity.status(HttpStatus.OK).location(URI.create("/api/users/login") ).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        UserDto userDto = userService.getUserDTOByEmail(email);
        if (userDto != null && userService.authenticateUser(email, password)) {
            SecurityContextHolder.setContext(SecurityContextHolder.getContext());
            System.out.println("/login CONTEXT!" + SecurityContextHolder.getContext());
            return ResponseEntity.status(HttpStatus.OK).location(URI.create("/api/users/profile")).body(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/home"))
                .body(userDto);
    }


    @GetMapping(path = "/local/profile")
    public ResponseEntity<UserDto> getLocalUserProfile(){
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestBody Principal principal) {
        System.out.println("CONTEXT = " + SecurityContextHolder.getContext());
        if (principal instanceof OAuth2AuthenticationToken authToken) {
            String email = authToken.getPrincipal().getAttribute("email");
            UserDto user = userService.getUserDTOByEmail(email);
            System.out.println("hej");
            return ResponseEntity.ok(user);
        }
        else if(principal instanceof Authentication auth){
            UserDto userDto = userService.getUserDTOByUsername(auth.getPrincipal().toString());
            System.out.println("INSTANCEOF Authentication = " + auth);
            return ResponseEntity.ok(userDto);
        }
        System.out.println("PRINCIPAL = "+ principal);
        System.out.println("INSTANCE OF class ==>"+ SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass());
        System.out.println("CONTEXT = " + SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/getUser/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable @Valid String username){
        UserDto u = userService.getUserDTOByUsername(username);
        if(u == null){
            throw new UserNotFoundException("No registered user with that username.");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/getUser/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable @Valid Integer id){
        UserDto u = userService.getUserDTOById(id);
        if(u == null) {
            throw new UserNotFoundException("No registered user with that id.");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(path="/allUsers")
    public ResponseEntity<Iterable<User>> getUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

}

