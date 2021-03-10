package com.l2yy.webgis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WebGisApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebGisApplication.class, args);
    }

}
