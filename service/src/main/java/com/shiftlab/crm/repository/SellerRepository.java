package com.shiftlab.crm.repository;

import com.shiftlab.crm.model.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    @Query(
            value = "SELECT s.id AS id, s.name AS name, s.contactInfo AS contactInfo, " +
                    "s.registrationDate AS registrationDate, COUNT(t.id) AS transactionsCount " +
                    "FROM Seller s LEFT JOIN s.transactions t " +
                    "GROUP BY s.id, s.name, s.contactInfo, s.registrationDate",
            countQuery = "SELECT COUNT(s) FROM Seller s"
    )
    Page<SellerCountProjection> findAllWithTransactionCount(Pageable pageable);

    @Query("SELECT s.id AS id, s.name AS name, s.contactInfo AS contactInfo, " +
            "s.registrationDate AS registrationDate, COUNT(t.id) AS transactionsCount " +
            "FROM Seller s LEFT JOIN s.transactions t " +
            "WHERE (SELECT COALESCE(SUM(pt.amount), 0) FROM Transaction pt " +
            "WHERE pt.seller = s AND pt.transactionDate BETWEEN :start AND :end) < :max_amount " +
            "GROUP BY s.id, s.name, s.contactInfo, s.registrationDate")
    List<SellerCountProjection> findSellersWithTotalAmountBelowWithCount(
            @Param("max_amount") BigDecimal maxAmount,
            @Param("start") LocalDateTime startDate,
            @Param("end") LocalDateTime endDate
    );
}