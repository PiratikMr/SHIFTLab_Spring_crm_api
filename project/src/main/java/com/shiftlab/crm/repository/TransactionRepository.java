package com.shiftlab.crm.repository;

import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySeller(Seller seller);

    @Query("SELECT t.seller FROM Transaction t WHERE t.transactionDate >= :start AND t.transactionDate <= :end GROUP BY t.seller ORDER BY SUM(t.amount) DESC LIMIT 1")
    Optional<Seller> findMostProductiveSellerByTotalAmount(@Param("start") LocalDateTime startDate, @Param("end") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.seller = :seller AND t.transactionDate >= :start AND t.transactionDate <= :end")
    BigDecimal sumTotalAmountBySellerAndDateRange(@Param("seller") Seller seller, @Param("start") LocalDateTime startDate, @Param("end") LocalDateTime endDate);
}