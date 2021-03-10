package com.l2yy.webgis.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageableResultVO<T> {

    private boolean hasMore;

    private long total;

    private List<T> data;

    public PageableResultVO(boolean hasMore, long total, List<T> data) {
        this.hasMore = hasMore;
        this.total = total;
        this.data = data;
    }
}
