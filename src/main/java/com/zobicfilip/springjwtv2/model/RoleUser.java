package com.zobicfilip.springjwtv2.model;

import com.zobicfilip.springjwtv2.keys.RoleUserCompKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table(name = "application_role_user", schema = "auth_db")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUser {

    @EmbeddedId
    private RoleUserCompKey id;

    @Column(nullable = false)
    @CreatedDate // @CreationTimestamp in hibernate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false) // column joins // TOOD take a look at this try to make it mutable
    private User user;
    // this happens because we reference it by id column in embedded key so we need insertable and updatable when saving use keys

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "title", insertable = false, updatable = false) // manytomany table ide
    private Role role;

    public RoleUser(RoleUserCompKey id) {
        this.id = id;
    }
    // this happens because we reference it by id column in embedded key so we need insertable and updatable when saving use keys

    public Set<String> getRolesAndAuthorities() {
        Set<String> set = new HashSet<>();
        set.add(this.getRole().getTitle());
        this.getRole().getPermissions().forEach(p -> set.add(p.getTitle()));
        return set;
    }

    @Override
    public String toString() {
        return "RoleUser{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", user=" + user +
                ", role=" + role +
                '}';
    }
}
