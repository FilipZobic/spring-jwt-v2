package com.zobicfilip.springjwtv2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Table(name = "application_role", schema = "auth_db")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    private String title;

    @ManyToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinTable(
            schema = "auth_db",
            name = "application_role_permission",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "title"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "title")
    )
    private Collection<Permission> permissions;
}
