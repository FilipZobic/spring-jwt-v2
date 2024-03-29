package com.zobicfilip.springjwtv2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;

@Table(name = "application_permission", schema = "auth_db")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    private String title;

    @ManyToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinTable(
            schema = "auth_db",
            name = "application_role_permission",
            joinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "title"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "title")
    )
    private Collection<Role> roles;

    @Override
    public String toString() {
        return "Permission{" +
                "title='" + title + '\'' +
                ", roles=" + roles.stream().map(Role::getTitle).collect(Collectors.joining(", ")) +
                '}';
    }
}
