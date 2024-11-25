package com.moderathowinther.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.moderathowinther.usermanagement.role.Role;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {

    @NotBlank(message = "Email field is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NonNull
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    @NonNull
    @NotBlank(message = "Name field is required.")
    private String name;

    private Set<Role> roles = new HashSet<>();

    @NotBlank(message = "Password field is required.")
    @Size(min = 8, max = 40, message = "Password must be between 8 and 40 characters.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public UserDto(@NonNull String email, @NonNull String username, @NonNull String name, String password) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDTO = (UserDto) o;
        return Objects.equals(email, userDTO.email) && Objects.equals(username, userDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, username);
    }
}
