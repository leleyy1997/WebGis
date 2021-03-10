package com.l2yy.webgis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/15 4:52 下午
 * @description：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@TableName("ftx_house_info")
public class FtxHouseInfoDO {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String price;

    private String address;

    private String status;

    private String phone;

    private double score;

    private String mainType;

    private String url;

    private String units;

    private double lon;

    private double lat;

    private Long cityId;
}
