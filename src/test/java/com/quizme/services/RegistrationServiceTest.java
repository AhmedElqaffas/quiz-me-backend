package com.quizme.services;

import com.quizme.dto.ApiError;
import com.quizme.dto.RegisterCredentialsRequestDto;
import com.quizme.entities.User;
import com.quizme.entities.UserCredentials;
import com.quizme.repos.UserRepo;
import com.quizme.services.result.Failure;
import com.quizme.services.result.FailureReason;
import com.quizme.services.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserCredentialsService userCredentialsService;
    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void register_registersNewUser_whenUniqueUsernameAndEmail(){
        var request = new RegisterCredentialsRequestDto("u", "e", "pw");
        when(userRepo.findByEmail("e")).thenReturn(Optional.empty());
        when(userRepo.findByUsername("u")).thenReturn(Optional.empty());
        // simulate db transaction successful
        when(transactionTemplate.execute(any())).thenAnswer(_ -> new User("e", "u"));

        var result = registrationService.register(request);

        assertEquals("u", result.success().getUsername());
        assertEquals("e", result.success().getEmail());
    }

    @Test
    void register_returnsError_whenUsernameExists(){
        var username = "x";
        var existingUser = new User("e", username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(existingUser));

        var result = registrationService.register(new RegisterCredentialsRequestDto(username, "e", "pw"));

        assertEquals(Result.failure(new Failure(FailureReason.ALREADY_EXISTS, "Username already in use")), result);
    }

    @Test
    void register_returnsError_whenEmailAndCredentialsExist(){
        var email = "e";
        var existingUser = new User(email, "x");
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userCredentialsService.findByUserId(existingUser)).thenReturn(Optional.of(new UserCredentials(existingUser, "pw")));

        var result = registrationService.register(new RegisterCredentialsRequestDto("x", email, "pw"));

        assertEquals(Result.failure(new Failure(FailureReason.ALREADY_EXISTS, "This email is already registered")), result);
    }

    @Test
    void register_linksCredentialsToUser_whenEmailExistsButNoCredentials(){
        var email = "e";
        var existingUsername = "oldUsername";
        var existingUser = new User(email, existingUsername);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userCredentialsService.findByUserId(existingUser)).thenReturn(Optional.empty());

        var result = registrationService.register(new RegisterCredentialsRequestDto("newUsername", email, "pw"));

        assertEquals(email, result.success().getEmail());
        // existing username should not be overridden by the new username, we should ignore the new username.
        assertEquals(existingUsername, result.success().getUsername());


    }
}
