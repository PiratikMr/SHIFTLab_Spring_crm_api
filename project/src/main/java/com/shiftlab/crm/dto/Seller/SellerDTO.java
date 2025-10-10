package com.shiftlab.crm.dto.Seller;

import com.shiftlab.crm.dto.Transaction.TransactionShortDTO;
import com.shiftlab.crm.model.Seller;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SellerDTO extends SellerShortDTO {
    private List<TransactionShortDTO> transactions;

    public SellerDTO(Seller seller) {
        super(seller);
        if (getTransactionsCount() != 0) {
            transactions = seller.getTransactions().stream()
                    .map(TransactionShortDTO::new)
                    .collect(Collectors.toList());
        }
    }
}
