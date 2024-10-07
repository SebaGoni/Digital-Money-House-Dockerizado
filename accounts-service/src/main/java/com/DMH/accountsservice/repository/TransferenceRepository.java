package com.DMH.accountsservice.repository;

import com.DMH.accountsservice.entities.Transference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferenceRepository extends JpaRepository<Transference, Long> {
    List<Transference> findTop5ByAccountIdOrderByDateDesc(Long accountId);
}

