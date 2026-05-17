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

    @Query("SELECT s FROM Seller s WHERE " +
           "(SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.seller = s AND t.transactionDate BETWEEN :start AND :end) < :max_amount")
    List<Seller> findSellersWithTotalAmountBelow(
            @Param("max_amount") BigDecimal maxAmount,
            @Param("start") LocalDateTime startDate,
            @Param("end") LocalDateTime endDate
    );
}