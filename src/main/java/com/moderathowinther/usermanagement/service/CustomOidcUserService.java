package com.moderathowinther.usermanagement.service;

import lombok.RequiredArgsConstructor;
import com.moderathowinther.usermanagement.oidc.GoogleUserData;
import com.moderathowinther.usermanagement.repository.UserRepository;
import com.moderathowinther.usermanagement.role.Role;
import com.moderathowinther.usermanagement.user.User;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        GoogleUserData googleUserData = new GoogleUserData(oidcUser.getAttributes());

        Optional<User> userOptional = userRepository.findByEmail(googleUserData.getEmail());
        if (userOptional.isEmpty()) {
            User user = new User(googleUserData.getEmail(), googleUserData.getName() + UUID.randomUUID().toString().substring(0, 5), googleUserData.getName(), "oauth2login");
            user.getRoles().add(new Role("MEMBER"));
            userRepository.save(user);
        }
        return oidcUser;
    }




}
