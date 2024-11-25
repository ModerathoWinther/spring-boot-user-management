package com.moderathowinther.usermanagement.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import com.moderathowinther.usermanagement.dto.UserDto;
import com.moderathowinther.usermanagement.exception.InvalidUserInputException;
import com.moderathowinther.usermanagement.exception.UserAlreadyExistsException;
import com.moderathowinther.usermanagement.exception.UserNotFoundException;
import com.moderathowinther.usermanagement.mapper.UserMapper;
import com.moderathowinther.usermanagement.repository.RoleRepository;
import com.moderathowinther.usermanagement.repository.UserRepository;
import com.moderathowinther.usermanagement.role.Role;
import com.moderathowinther.usermanagement.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService extends DefaultOAuth2UserService {

    private final RoleRepository roleRepository;
    private final Validator validator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(RoleRepository roleRepository, Validator validator, UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.validator = validator;
    }
    @Transactional
    public User registerNewUser(@Valid UserDto userDto) {
        validateUserDTO(userDto);
        checkUsernameAvailability(userDto);
        User u = new User(userDto.getEmail(),userDto.getName(), userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(addDefaultRoleToUser(u));
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");

        if (email != null && userRepository.findByEmail(email).isEmpty()) {
            User newUser = new User(email, oAuth2User.getName(), oAuth2User.getName(), passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.getRoles().add(getOrCreateDefaultRole());
            userRepository.save(newUser);
        }
        return oAuth2User;
    }

    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new UserNotFoundException("No registered users yet.");
        }
        return users;
    }

    public UserDto getUserDTOByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("No registered user with that username."));
    }

    public UserDto getUserDTOById(Integer id) {
        return userRepository.findById(id)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("No registered user with that ID."));
    }

    public UserDto getUserDTOByEmail(String email){
        Optional<User> u = userRepository.findByEmail(email);
        if(u.isPresent()){
            return UserMapper.toDTO(u.get());
        }
        throw new UserNotFoundException("No registered user with that email.");
    }

    public boolean authenticateUser(String email, String password){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.filter(value -> passwordEncoder.matches(password, value.getPassword())).isPresent()){
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(optionalUser.get().getUsername(), null, optionalUser.get().getRoles()
                            .stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .toList());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return true;
        }
        return false;

    }

    private void validateUserDTO(UserDto userDTO){
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDTO);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidUserInputException(errorMessage, violations);
        }
    }

    private void checkUsernameAvailability(UserDto userDTO) {
        if(userRepository.findByUsername(userDTO.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("A user is already registered with that username.");
        }
    }

    private User addDefaultRoleToUser(User u) {
        u.getRoles().add(getOrCreateDefaultRole());
        return u;
    }

    private Role getOrCreateDefaultRole() {
        return roleRepository.findByName("ROLE_MEMBER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_MEMBER")));
    }
}
