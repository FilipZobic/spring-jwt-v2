package com.zobicfilip.springjwtv2.repository;

import com.zobicfilip.springjwtv2.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByEmailOrUsername(String email, String username);

    @Query("""
SELECT user FROM User user
WHERE (:username IS NULL OR UPPER(user.username) LIKE UPPER(concat('%',:username,'%') ) )
AND (:email IS NULL OR UPPER(user.email) LIKE UPPER(concat('%',:email,'%') ) )
AND (:countryTag IS NULL OR UPPER(user.countryTag) = :countryTag )
""")
    Page<User> findAll(Pageable pageable, String username, String email, String countryTag);

    @Transactional
    @Modifying
    void deleteByUsername(String username);
}
