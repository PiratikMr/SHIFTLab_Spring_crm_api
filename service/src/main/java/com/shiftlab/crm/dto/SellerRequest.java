package com.shiftlab.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SellerRequest {

    @NotBlank(message = "Имя продавца не может быть пустым")
    private String name;

    private String contactInfo;
}
