package com.quizme.utils;

import com.quizme.config.AppProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PasswordHasher {

    @Autowired
    private AppProperties appProperties;

    /**
     * Hash a password using Argon2 algorithm.
     * A pepper is added to the password before hashing for additional security.
     * @param password the password to hash
     * @return the hashed password
     */
    @NonNull
    public String hashPassword(@NonNull String password) {
        var pepperedPassword = password + appProperties.getAuth().getPepper();
        Argon2PasswordEncoder arg2SpringSecurity = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        // The encode method returns null only if the input is null, which we already avoid by
        // annotating the parameter as @NonNull.
        return Objects.requireNonNull(arg2SpringSecurity.encode(pepperedPassword));
    }
}
