package com.l2yy.webgis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/27 12:54 上午
 * @description：
 */
@Data
public class UpdateLogDTO {

    private String content;

    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime timestamp;

    private String color;
}
