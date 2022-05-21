package com.zobicfilip.springjwtv2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "application_user")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id; // source of truth

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String countryTag; // alpha2

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    @CreatedDate // @CreationTimestamp in hibernate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate // @UpdateTimestamp in hibernate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean enabled;
}
