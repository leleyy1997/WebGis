package com.l2yy.webgis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l2yy.webgis.constant.CommonConstant;
import com.l2yy.webgis.dto.CityListDTO;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.FtxCityListDO;
import com.l2yy.webgis.entity.FtxHouseInfoDO;
import com.l2yy.webgis.mapper.FtxCityListMapper;
import com.l2yy.webgis.service.FtxCityListService;
import com.l2yy.webgis.service.FtxHouseInfoService;
import com.l2yy.webgis.util.PageableResultVO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/15 1:54 下午
 * @description：
 */
@Service
public class FtxCityListServiceImpl extends ServiceImpl<FtxCityListMapper, FtxCityListDO> implements FtxCityListService {

    private Logger logger = LoggerFactory.getLogger(FtxCityListServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FtxHouseInfoService ftxHouseInfoService;


    @Override
    public void getFtxCityList() {
        try {
            String url = "https://static.soufunimg.com/homepage/new/family/css/citys2018061301.js?v=20190522";

            CloseableHttpClient httpclient = HttpClients.createDefault();

            // 创建HttpGet请求，相当于在浏览器输入地址
            HttpGet httpGet = new HttpGet(url);

            CloseableHttpResponse response = null;
            try {
                // 执行请求，相当于敲完地址后按下回车。获取响应
                response = httpclient.execute(httpGet);
                // 判断返回状态是否为200
                if (response.getStatusLine().getStatusCode() == 200) {
                    // 解析响应，获取数据
                    String content = EntityUtils.toString(response.getEntity(), "UTF-8");

                    String replace = content.replace("var cityJson = ", "").replace("//", "").replace(";", "").replace("http:", "");

                    List<FtxCityListDO> listDOS = JSONObject.parseArray(replace, FtxCityListDO.class);

                    this.saveBatch(listDOS);

                }
            } finally {
                if (response != null) {
                    // 关闭资源
                    response.close();
                }
                // 关闭浏览器
                httpclient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageableResultVO<CityListDTO> FtxCityList(QueryListDTO<CityListDTO> queryListDTO) {
        LambdaQueryWrapper<FtxCityListDO> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (queryListDTO.getData() != null) {
            if (queryListDTO.getData().getCity() != null && !StringUtils.trimAllWhitespace(queryListDTO.getData().getCity()).isEmpty())
                lambdaQueryWrapper.like(FtxCityListDO::getName, StringUtils.trimAllWhitespace(queryListDTO.getData().getCity()));
        }
        IPage<FtxCityListDO> page = new Page<>();
        page.setCurrent(queryListDTO.getPage());
        page.setSize(queryListDTO.getPageSize());

        IPage<FtxCityListDO> cityPage = this.page(page, lambdaQueryWrapper);

        List<CityListDTO> collect = cityPage.getRecords().stream().map(ftxCityListDO ->
                new CityListDTO(ftxCityListDO.getId(), ftxCityListDO.getName(), ftxCityListDO.getSpell(), "https://" + ftxCityListDO.getUrl(), ftxCityListDO.getHot(), ftxCityListDO.getHaveData())).collect(Collectors.toList());
        return new PageableResultVO<>(true, cityPage.getTotal(), collect);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public Boolean delCityData(Long cityId) {
        boolean update = this.lambdaUpdate().eq(FtxCityListDO::getId, cityId).set(FtxCityListDO::getHaveData, CommonConstant.HAVE_NO_DATA).update();

        LambdaQueryWrapper<FtxHouseInfoDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FtxHouseInfoDO::getCityId, cityId);
        boolean remove = ftxHouseInfoService.remove(lambdaQueryWrapper);

        return remove;
    }
}



