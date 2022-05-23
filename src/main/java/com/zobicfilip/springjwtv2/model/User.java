package com.zobicfilip.springjwtv2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Table(name = "application_user", schema = "auth_db")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "pg-uuid")
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

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true,
    /*mappedBy = "user"*/ // attribute in
    mappedBy = "id.userId") // attribute in composite key
    private Collection<RoleUser> roles;

    public void addRole(RoleUser userRole) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(userRole);
    }

    public Set<String> getRolesInStringSet() {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        return this.getRoles().stream()
                .map(RoleUser::getRolesAndAuthorities)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
