package com.example.demo;

import io.mateu.remote.dtos.Journey;
import io.mateu.remote.dtos.JourneyCreationRq;
import io.mateu.remote.dtos.Step;
import io.mateu.remote.dtos.UI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
public class Servicio {

    public void getPerson() {
        long t0 = System.currentTimeMillis();

        // pedimos la UI (los menús, la home, ...)
        UI ui = new RestTemplate()
                .getForObject("https://explorer.mateu.io/mateu/v1/uis/com.example.demoremote.ui.demoApp.Home",
                        UI.class);

        // arrancamos un journey
        String journeyId = UUID.randomUUID().toString();
        new RestTemplate()
                .postForObject("https://explorer.mateu.io/mateu/v1/journeys/nfl_teams/" + journeyId
                , JourneyCreationRq.builder().build(), String.class);

        // preguntamos el estado del journey
        Journey journey = new RestTemplate()
                .getForObject("https://explorer.mateu.io/mateu/v1/journeys/nfl_teams/" + journeyId
                        , Journey.class);

        // cogemos el id del step
        String stepId = journey.getCurrentStepId();

        // pedimos el step
        Step step = new RestTemplate()
                .getForObject("https://explorer.mateu.io/mateu/v1/journeys/nfl_teams/"
                                + journeyId+ "/steps/" + stepId
                        , Step.class);


        log.info("people in " + (System.currentTimeMillis() - t0) + "ms");

    }

}
