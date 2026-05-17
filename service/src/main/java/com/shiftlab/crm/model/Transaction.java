package com.shiftlab.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @DecimalMin(value = "0", message = "Сумма транзакции не может быть отрицательной")
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    public enum PaymentType {
        CASH, CARD, TRANSFER
    }
}