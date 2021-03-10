package com.l2yy.webgis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/5 12:27 上午
 * @description：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryListDTO<T> {

    private int page = 1;

    private int pageSize = 10;

    private String sort;

    private boolean asc = true;

    private T data;
}
