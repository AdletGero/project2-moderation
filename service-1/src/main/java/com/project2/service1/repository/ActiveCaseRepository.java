package com.project2.service1.repository;

import com.project2.service1.entity.ActiveCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveCaseRepository extends JpaRepository<ActiveCase, Long> {
     boolean existsByClientIdAndCategoryAndStatus(String clientId, String category, String status);
}
