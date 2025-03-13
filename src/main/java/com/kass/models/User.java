package com.kass.models;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 50)
    private String name;

    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 50)
    private String lastname;

    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 50)
    private String username;

    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 150)
    private String password;

    @ManyToMany
    @JoinTable(name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"),
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})})
    private List<Role> roles;

    private boolean enabled;

    public User() {
        this.roles = new ArrayList<>();
    }

}
