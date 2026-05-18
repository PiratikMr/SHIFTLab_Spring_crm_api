package com.shiftlab.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Страница с результатами")
@Data
public class CustomPage<T> {

    @Schema(description = "Список элементов на странице")
    private List<T> content;

    @Schema(description = "Текущая страница (с 0)")
    private int page;

    @Schema(description = "Всего страниц")
    private int pages;

    @Schema(description = "Элементов на странице")
    private int perPage;

    @Schema(description = "Всего элементов")
    private long totalElements;

    public CustomPage(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.pages = page.getTotalPages();
        this.perPage = page.getSize();
        this.totalElements = page.getTotalElements();
    }
}
