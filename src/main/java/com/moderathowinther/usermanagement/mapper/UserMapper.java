package com.moderathowinther.usermanagement.mapper;

import com.moderathowinther.usermanagement.dto.UserDto;
import com.moderathowinther.usermanagement.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserDto toDTO(User user){
        if(user == null){
            return new UserDto();
        }
        return UserDto.builder()
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .password(null)
                .build();
    }

    public static User toEntity(UserDto userDTO){
        if(userDTO == null){
            return new User();
        }
        return User.builder()
                .name(userDTO.getName())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .roles(userDTO.getRoles())
                .password(null)
                .build();
    }
}
