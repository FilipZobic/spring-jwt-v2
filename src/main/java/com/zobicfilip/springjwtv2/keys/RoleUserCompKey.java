package com.zobicfilip.springjwtv2.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserCompKey implements Serializable {

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "user_id")
    private UUID userId;
}
