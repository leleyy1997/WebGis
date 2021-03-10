package com.l2yy.webgis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/7 5:17 下午
 * @description：
 */
@Data
public class ProgressRateDTO {

    private Long total;

    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime time ;

    private long current;

}
