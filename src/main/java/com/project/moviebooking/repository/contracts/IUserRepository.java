package com.project.moviebooking.repository.contracts;

import com.project.moviebooking.model.User;

import java.util.Optional;

/** DIP abstraction for user persistence. */
public interface IUserRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
