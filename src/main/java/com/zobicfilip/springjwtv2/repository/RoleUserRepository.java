package com.zobicfilip.springjwtv2.repository;

import com.zobicfilip.springjwtv2.keys.RoleUserCompKey;
import com.zobicfilip.springjwtv2.model.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleUserRepository extends JpaRepository <RoleUser, RoleUserCompKey> {
}
