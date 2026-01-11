package com.quizme.entities;

import com.quizme.utils.uuid.GeneratedUuidV7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedUuidV7
    private UUID id;
    private String email;
    private String username;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
