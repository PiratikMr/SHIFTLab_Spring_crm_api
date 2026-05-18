package com.shiftlab.crm.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип оплаты")
public enum PaymentType {
    CASH, CARD, TRANSFER
}
