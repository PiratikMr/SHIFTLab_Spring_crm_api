package com.shiftlab.crm.dto;

import com.shiftlab.crm.model.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Данные для создания / обновления транзакции")
@Data
@NoArgsConstructor
public class TransactionRequest {

    @Schema(description = "Сумма транзакции", example = "1500.00")
    @NotNull(message = "Сумма транзакции обязательна")
    @DecimalMin(value = "0", message = "Сумма транзакции не может быть отрицательной")
    private BigDecimal amount;

    @Schema(description = "Тип оплаты")
    @NotNull(message = "Тип оплаты обязателен")
    private PaymentType paymentType;
}
