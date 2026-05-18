package com.shiftlab.crm.dto.seller;

import com.shiftlab.crm.dto.transaction.TransactionShortDTO;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SellerDTO extends SellerShortDTO {
    private List<TransactionShortDTO> transactions;

    public SellerDTO(Seller seller) {
        super(seller);
        List<Transaction> txList = seller.getTransactions();
        transactions = txList != null
                ? txList.stream().map(TransactionShortDTO::new).toList()
                : List.of();
    }
}
