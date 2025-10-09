package com.shiftlab.crm.dto.Seller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.shiftlab.crm.dto.Transaction.TransactionShortDTO;
import com.shiftlab.crm.model.Seller;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SellerDTO {
    @JsonUnwrapped
    private SellerShortDTO sellerShortDTO;
    private List<TransactionShortDTO> transactions;

    public SellerDTO(Seller seller) {
        this.sellerShortDTO = new SellerShortDTO(seller);
        if (sellerShortDTO.getTransactionsCount() != 0) {
            transactions = seller.getTransactions().stream()
                    .map(t -> new TransactionShortDTO(t))
                    .collect(Collectors.toList());

        }
    }
}
