package com.example.faab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.faab.mapper")
public class FaabApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaabApplication.class, args);
    }

}
