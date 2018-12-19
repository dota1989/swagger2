package com.zb.myswagger2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class Myswagger2Application {

    public static void main(String[] args) {
        SpringApplication.run(Myswagger2Application.class, args);
    }

}

