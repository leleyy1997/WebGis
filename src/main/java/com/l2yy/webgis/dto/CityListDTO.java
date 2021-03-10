package com.l2yy.webgis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/22 12:28 下午
 * @description： TODO 应该为整个项目城市列表的公用DTO，不能直接返回DO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityListDTO {

    private Long id;

    private String city;

    private String keyword;

    private String url;

    private byte hot;

    private byte haveData;

}
