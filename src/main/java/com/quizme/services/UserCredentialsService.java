package com.quizme.services;

import com.quizme.entities.User;
import com.quizme.entities.UserCredentials;
import com.quizme.repos.UserCredentialsRepo;
import com.quizme.utils.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCredentialsService {
    private final UserCredentialsRepo userCredentialsRepo;
    private final PasswordHasher passwordHasher;

    public UserCredentialsService (UserCredentialsRepo userCredentialsRepo,
                                  PasswordHasher passwordHasher) {
        this.userCredentialsRepo = userCredentialsRepo;
        this.passwordHasher = passwordHasher;
    }

    public Optional<UserCredentials> findByUserId(User user) {
        return userCredentialsRepo.findByUserId(user);
    }

    /**
     * Create and save UserCredentials for the given user with the provided password.
     * The password is hashed before being stored.
     * @param user the User entity to associate the credentials with
     * @param password the plaintext password to be hashed and stored
     * @return the saved UserCredentials entity
     */
    public UserCredentials createCredentialsForUser(User user, String password) {
        var userCredentials = new UserCredentials(user, passwordHasher.hashPassword(password));
        return userCredentialsRepo.save(userCredentials);
    }
}
