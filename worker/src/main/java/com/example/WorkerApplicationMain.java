package com.example;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WorkerApplicationMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(WorkerApplicationMain.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}
