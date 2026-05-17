package com.shiftlab.crm.dto.Seller;

import com.shiftlab.crm.dto.Transaction.TransactionShortDTO;
import com.shiftlab.crm.model.Seller;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SellerDTO extends SellerShortDTO {
    private List<TransactionShortDTO> transactions;

    public SellerDTO(Seller seller) {
        super(seller);
        transactions = getTransactionsCount() == 0
                ? Collections.emptyList()
                : seller.getTransactions().stream().map(TransactionShortDTO::new).toList();
    }
}
