package com.hood.merch.security.domain;

import com.hood.merch.security.domain.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
}
