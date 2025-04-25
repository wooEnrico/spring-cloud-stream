package com.example;


import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class RestApplicationMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(RestApplicationMain.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}