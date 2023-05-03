package com.example.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("")
@Slf4j
public class Controlador {

    @GetMapping
    public Mono<String> get() {
        log.info("get", Map.of("a", 1, "b", 2));
        return Mono.just("Hola!").doOnSuccess((s) -> {
            log.info("done");
        });
    }

}
