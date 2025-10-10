package com.shiftlab.crm.repository;

import com.shiftlab.crm.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    @Query("SELECT s FROM Seller s LEFT JOIN Transaction t ON t.seller = s WHERE t.transactionDate BETWEEN :start AND :end OR t.id is null GROUP BY s HAVING SUM(t.amount) < :max_amount OR SUM(t.amount) IS NULL")
    List<Seller> findSellersWithTotalAmountBelow(
            @Param("max_amount") BigDecimal maxAmount,
            @Param("start") LocalDateTime startDate,
            @Param("end") LocalDateTime endDate
    );
}