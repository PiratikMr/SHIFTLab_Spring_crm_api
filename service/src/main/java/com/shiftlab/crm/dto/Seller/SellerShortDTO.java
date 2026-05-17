package com.shiftlab.crm.dto.Seller;

import com.shiftlab.crm.model.Seller;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SellerShortDTO {
    private Long id;
    private String name;
    private String contactInfo;
    private LocalDateTime registrationDate;
    private int transactionsCount;

    public SellerShortDTO(Seller seller) {
        this.id = seller.getId();
        this.name = seller.getName();
        this.contactInfo = seller.getContactInfo();
        this.registrationDate = seller.getRegistrationDate();
        this.transactionsCount = seller.getTransactions() != null ? seller.getTransactions().size() : 0;
    }
}
