package com.spendsense.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String userId;      // like "rahul_01"

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;    // stored as BCrypt hash
}