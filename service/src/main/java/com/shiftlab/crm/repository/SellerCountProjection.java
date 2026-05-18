package com.shiftlab.crm.repository;

import java.time.LocalDateTime;

public interface SellerCountProjection {
    Long getId();

    String getName();

    String getContactInfo();

    LocalDateTime getRegistrationDate();

    Long getTransactionsCount();
}
