package com.quizme.services;

import com.quizme.dto.RegisterCredentialsRequestDto;
import com.quizme.entities.User;
import com.quizme.repos.UserRepo;
import com.quizme.services.result.Failure;
import com.quizme.services.result.FailureReason;
import com.quizme.services.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class RegistrationService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserCredentialsService userCredentialsService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * Register a user with given credentials.<br>
     * The user may already exist as they may have signed up via OAuth.
     * In that case, we link the credentials to the existing user.
     * Otherwise, we create a new user and link the credentials to them.
     * @param request RegisterCredentialsRequestDto containing user registration info
     * @return Result<User> containing the registered user or error information
     */
    public Result<User> register(RegisterCredentialsRequestDto request){
        var userWithSameEmail = userRepo.findByEmail(request.email());
        if(userWithSameEmail.isPresent()) {
            return handleExistingEmail(request, userWithSameEmail.get());
        }

        var usernameExists = userRepo.findByUsername(request.username()).isPresent();
        if(usernameExists) {
            return Result.failure(new Failure(FailureReason.ALREADY_EXISTS, "Username already in use"));
        }

        return Result.success(registerNewUserAndCredentials(request));
    }

    /**
     * Handle the case where a user with the same email already exists.
     * Either link credentials to existing user or return an error if credentials already exist.
     * @param request RegisterCredentialsRequestDto containing user registration info
     * @param userWithSameEmail the existing user with the same email
     * @return Result<User> containing the user or error information
     */
    private Result<User> handleExistingEmail(RegisterCredentialsRequestDto request, User userWithSameEmail) {
        var credentials = userCredentialsService.findByUserId(userWithSameEmail);
        if (credentials.isPresent()) {
            return Result.failure(new Failure(FailureReason.ALREADY_EXISTS, "This email is already registered"));
        }
        // Link new credentials to the existing user
        userCredentialsService.createCredentialsForUser(userWithSameEmail, request.password());
        return Result.success(userWithSameEmail);
    }

    private User registerNewUserAndCredentials(RegisterCredentialsRequestDto request) {
        // We use a transaction to ensure both user and credentials are created atomically
        // @Transactional annotation cannot be used because the method is called from within the same class
        // and thus would not be proxied by Spring for transaction management
        // so we use TransactionTemplate instead
        var transactionResult = transactionTemplate.execute( (TransactionCallback<Object>) _-> {
            User user = userRepo.save(new User(request.email(), request.username()));
            userCredentialsService.createCredentialsForUser(user, request.password());
            return user;
        });

        return (User) transactionResult;
    }
}
