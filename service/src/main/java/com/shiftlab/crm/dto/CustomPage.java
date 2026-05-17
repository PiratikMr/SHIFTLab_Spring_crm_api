package com.shiftlab.crm.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class CustomPage<T> {
    private List<T> content;

    private int page;
    private int pages;
    private int perPage;
    private long totalElements;

    public CustomPage(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.pages = page.getTotalPages();
        this.perPage = page.getSize();
        this.totalElements = page.getTotalElements();
    }
}
