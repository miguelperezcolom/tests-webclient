package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@Slf4j
public class DemoApplication {

    @Autowired
    Servicio servicio;


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Bean
    CommandLineRunner runner() {
        return (args) -> {


            for (int j = 0; j < 5; j++) {
                new Thread(() -> {
                    for (int i = 0; i < 30; i++) {
                        servicio.getPerson();
                    }
                }).start();
            }

            Thread.sleep(15000);
            log.info("done!!!!");

        };
    }

}
