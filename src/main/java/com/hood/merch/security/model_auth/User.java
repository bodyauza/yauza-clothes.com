package com.hood.merch.security.model_auth;

import com.hood.merch.security.domain.JwtRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String login;
    @Column
    private char password;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String role;

    public User() {

    }

    public User(String login, char password, String firstName, String lastName, String role) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return password == user.password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }
}