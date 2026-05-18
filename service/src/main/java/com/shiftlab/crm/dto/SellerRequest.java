package com.shiftlab.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Данные для создания / обновления продавца")
@Data
@NoArgsConstructor
public class SellerRequest {

    @Schema(description = "Имя продавца", example = "Иван Иванов")
    @NotBlank(message = "Имя продавца не может быть пустым")
    private String name;

    @Schema(description = "Контактная информация", example = "ivan@example.com")
    private String contactInfo;
}
