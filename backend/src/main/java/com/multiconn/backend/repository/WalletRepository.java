package com.multiconn.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multiconn.backend.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
}
