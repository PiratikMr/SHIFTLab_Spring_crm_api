package com.shiftlab.crm.dto.seller;

import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.repository.SellerCountProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Краткая информация о продавце")
@Data
@NoArgsConstructor
public class SellerShortDTO {

    @Schema(description = "ID продавца")
    private Long id;

    @Schema(description = "Имя продавца")
    private String name;

    @Schema(description = "Контактная информация")
    private String contactInfo;

    @Schema(description = "Дата регистрации")
    private LocalDateTime registrationDate;

    @Schema(description = "Количество транзакций")
    private int transactionsCount;

    public SellerShortDTO(Seller seller) {
        this.id = seller.getId();
        this.name = seller.getName();
        this.contactInfo = seller.getContactInfo();
        this.registrationDate = seller.getRegistrationDate();
        this.transactionsCount = seller.getTransactions() != null ? seller.getTransactions().size() : 0;
    }

    public SellerShortDTO(SellerCountProjection projection) {
        this.id = projection.getId();
        this.name = projection.getName();
        this.contactInfo = projection.getContactInfo();
        this.registrationDate = projection.getRegistrationDate();
        this.transactionsCount = projection.getTransactionsCount() != null
                ? projection.getTransactionsCount().intValue() : 0;
    }
}
