package com.hood.merch.security.repo_auth;

import com.hood.merch.security.model_auth.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();
}
