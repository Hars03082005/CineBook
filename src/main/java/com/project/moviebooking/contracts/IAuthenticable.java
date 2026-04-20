package com.project.moviebooking.contracts;

import com.project.moviebooking.dto.LoginRequest;
import com.project.moviebooking.dto.LoginResponse;

/**
 * SOLID: I (auth-only contract)
 * GRASP: Controller delegates auth workflow.
 */
public interface IAuthenticable {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    boolean validateCredentials(String email, String rawPassword);
}
