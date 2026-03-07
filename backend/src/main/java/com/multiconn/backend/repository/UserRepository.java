package com.multiconn.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multiconn.backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByGoogleAccountId(String googleAccountId);
}
