package com.l2yy.webgis;

import com.l2yy.webgis.service.FtxCityListService;
import com.l2yy.webgis.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//import org.junit.runner.RunWith;


//@RunWith(SpringRunner.class)
@SpringBootTest
class WebgisApplicationTests {

    @Autowired
    FtxCityListService ftxCityListService;

    @Autowired
    UserService userService;

//    @Test
//    void test(){
//        ftxCityListService.getFtxCityList();
//    }

    @Test
    void contextLoads() {
    }

}
