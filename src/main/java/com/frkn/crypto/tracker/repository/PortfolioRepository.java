package com.frkn.crypto.tracker.repository;

import com.frkn.crypto.tracker.model.PortfolioEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntry, Long> {

}
