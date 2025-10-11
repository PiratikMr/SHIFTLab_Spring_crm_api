package com.shiftlab.crm.repository;

import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySeller(Seller seller);
    Page<Transaction> findBySeller(Seller seller, Pageable pageable);

    @Query("SELECT t.seller FROM Transaction t WHERE t.transactionDate between :start AND :end GROUP BY t.seller ORDER BY SUM(t.amount) DESC LIMIT 1")
    Optional<Seller> findMostProductiveSellerByTotalAmount(
            @Param("start") LocalDateTime startDate,
            @Param("end") LocalDateTime endDate
    );
}