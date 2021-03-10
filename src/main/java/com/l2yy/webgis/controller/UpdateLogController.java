package com.l2yy.webgis.controller;


import com.l2yy.webgis.annotation.TokenCheck;
import com.l2yy.webgis.dto.UpdateLogDTO;
import com.l2yy.webgis.service.UpdateLogService;
import com.l2yy.webgis.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author huangjiale
 * @since 2020-03-27
 */
@RestController
@RequestMapping("/update-log-do")
public class UpdateLogController {

    @Autowired
    private UpdateLogService updateLogService;

    @TokenCheck
    @GetMapping("/log")
    public CommonResponse<List<UpdateLogDTO>> getLog(@RequestParam("reverse") Boolean reverse){
        return CommonResponse.buildSuccess(updateLogService.getLogList(reverse));
    }

    @GetMapping("/add-log")
    public CommonResponse<Boolean> addLog(@RequestParam("msg") String msg){
        return CommonResponse.buildSuccess(updateLogService.addLog(msg));
    }
}
