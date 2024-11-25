package com.moderathowinther.usermanagement.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import com.moderathowinther.usermanagement.role.Role;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@Table(name = "library_users")
public class User {

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    private String name;



    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // Added cascade option to manage save operations
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer ID;

    public User(@NonNull String email, @NonNull String username, @NonNull String name, String password) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, username);
    }
}
