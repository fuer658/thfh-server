package com.thfh.common;

import org.springframework.data.domain.Page;

import java.util.List;

public class CustomPage<T> {
    private List<T> content;
    private long total;
    private int totalPages;

    public CustomPage(Page<T> page) {
        this.content = page.getContent();
        this.total = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}