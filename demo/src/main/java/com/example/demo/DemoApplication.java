package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@Slf4j
public class DemoApplication {

    //public static final String BASE_URI = "http://localhost:8080";
    public static final String BASE_URI = "https://dmt-stg.wefox.io";
    public static final int MAX_THREADS = 10;
    public static final int MAX_FLOWS = 300;


    @Autowired
    LeadsFlowSimulator leadsFlowSimulator;

    @Autowired
    IntermediariesFlowSimulator intermediariesFlowSimulator;

    @Autowired
    DataRepo dataRepo;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Bean
    CommandLineRunner runner() {
        return (args) -> {

            LocalDateTime start = LocalDateTime.now();

            CountDownLatch countDownLatch = new CountDownLatch(MAX_THREADS);
            for (int j = 0; j < MAX_THREADS; j++) {
                new Thread(() -> {
                    try {
                        for (int i = 0; i < MAX_FLOWS; i++) {
                            String journeyId = UUID.randomUUID().toString();
                            if (i % 2 == 0) {
                                leadsFlowSimulator.simulateLeadsFlow(BASE_URI, journeyId);
                            } else {
                                intermediariesFlowSimulator.simulateIntermediariesFlow(BASE_URI, journeyId);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                }).start();
            }

            countDownLatch.await();
            LocalDateTime end = LocalDateTime.now();
            log.info("done!!!!");
            log.info("threads: " + MAX_THREADS);
            log.info("simulations: " + MAX_FLOWS);

            Duration diff = Duration.between(start, end);
            String hms = String.format("%d:%02d:%02d",
                    diff.toHours(),
                    diff.toMinutesPart(),
                    diff.toSecondsPart());
            log.info("total simulation time: " + hms);

            dataRepo.dump();

        };
    }

}
