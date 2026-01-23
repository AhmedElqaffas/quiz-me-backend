package com.quizme.utils;

import com.quizme.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    @Test
    public void testHashMatchesPepperedPassword() {
        AppProperties props = new AppProperties();
        props.getAuth().setPepper("pep");

        PasswordHasher hasher = new PasswordHasher(props);
        String hash = hasher.hashPassword("secret");

        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        assertTrue(encoder.matches("secretpep", hash));
    }

    @Test
    public void testHashesAreDifferentForSameInputDueToSalt() {
        AppProperties props = new AppProperties();
        props.getAuth().setPepper("pep");

        PasswordHasher hasher = new PasswordHasher(props);
        String hash1 = hasher.hashPassword("secret");
        String hash2 = hasher.hashPassword("secret");

        // The Argon2 encoder should produce different hashes because of random salt
        assertNotEquals(hash1, hash2);
    }
}
