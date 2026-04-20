package com.project.moviebooking.repository;

import com.project.moviebooking.model.User;
import com.project.moviebooking.repository.contracts.IUserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - MongoDB CRUD for User
 * SOLID: I - Interface Segregation
 */
@Repository
public interface UserRepository extends MongoRepository<User, String>, IUserRepository {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
