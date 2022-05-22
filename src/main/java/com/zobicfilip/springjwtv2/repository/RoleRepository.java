package com.zobicfilip.springjwtv2.repository;

import com.zobicfilip.springjwtv2.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findRoleByTitle(String title);
}
