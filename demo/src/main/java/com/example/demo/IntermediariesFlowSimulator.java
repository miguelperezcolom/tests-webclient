package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mateu.remote.dtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IntermediariesFlowSimulator {

    @Autowired
    DataRepo dataRepo;

    ObjectMapper mapper = new ObjectMapper();

    public void simulateIntermediariesFlow(String baseUri, String journeyId) throws JsonProcessingException {

        // pedimos la UI (los menús, la home, ...)
        UI ui = getUi(baseUri);

        // arrancamos un journey
        createJourney(baseUri, journeyId);

        // preguntamos el estado del journey
        Journey journey = getJourneyStatus(baseUri, journeyId);

        // cogemos el id del step
        String stepId = journey.getCurrentStepId();

        // pedimos el step
        Step step = getStep(baseUri, journeyId, stepId);

        // pedimos el count
        Long count = getCount(baseUri, journeyId, stepId);

        // pedimos las filas
        List<Map<String, Object>> list = getRows(baseUri, journeyId, stepId);

        // pedimos el step
        openLeadDetails(baseUri, journeyId, stepId, list.get(0));

        // preguntamos el estado del journey
        journey = getJourneyStatus(baseUri, journeyId);

        // cogemos el id del step
        stepId = journey.getCurrentStepId();

        // pedimos el step
        step = getStep(baseUri, journeyId, stepId);

    }


    private void openLeadDetails(String baseUri, String journeyId, String stepId, Map<String, Object> row) {
        log.info("call the edit action");
        long t0 = System.currentTimeMillis();
        String rs = new RestTemplate()
                .postForObject(baseUri + "/mateu/v1/journeys/intermediaries/"
                                + journeyId+ "/steps/" + stepId
                                + "/__list__main__edit"
                        , RunActionRq.builder()
                                .data(Map.of("_selectedRow", row))
                                .build()
                        , String.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
    }

    private List<Map<String, Object>> getRows(String baseUri, String journeyId, String stepId) {
        log.info("getting the list rows");
        long t0 = System.currentTimeMillis();
        List<Map<String, Object>> rows = new RestTemplate()
                .getForObject(baseUri + "/mateu/v1/journeys/intermediaries/"
                                + journeyId+ "/steps/" + stepId
                                + "/lists/main/rows?page=0&page_size=50&ordering=W10=&filters=e30="
                        , ArrayList.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
        return rows;
    }

    private Long getCount(String baseUri, String journeyId, String stepId) {
        log.info("getting the list count");
        long t0 = System.currentTimeMillis();
        Long count = new RestTemplate()
                .getForObject(baseUri + "/mateu/v1/journeys/intermediaries/"
                                + journeyId+ "/steps/" + stepId
                                + "/lists/main/count?filters=e30="
                        , Long.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
        return count;
    }

    private Step getStep(String baseUri, String journeyId, String stepId) {
        log.info("getting the current step");
        long t0 = System.currentTimeMillis();
        Step step = new RestTemplate()
                .getForObject(baseUri + "/mateu/v1/journeys/intermediaries/"
                                + journeyId+ "/steps/" + stepId
                        , Step.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
        return step;
    }

    private Journey getJourneyStatus(String baseUri, String journeyId) {
        log.info("getting the journey status");
        long t0 = System.currentTimeMillis();
        Journey journey = new RestTemplate()
                .getForObject(baseUri + "/mateu/v1/journeys/intermediaries/" + journeyId
                        , Journey.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
        return journey;
    }

    private void createJourney(String baseUri, String journeyId) {
        log.info("creating journey");
        long t0 = System.currentTimeMillis();
        new RestTemplate()
                .postForObject(baseUri + "/mateu/v1/journeys/intermediaries/" + journeyId
                        , JourneyCreationRq.builder().build(), String.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
    }

    private UI getUi(String baseUri) {
        log.info("asking for UI");
        long t0 = System.currentTimeMillis();
        UI ui = new RestTemplate()
                .getForObject(baseUri + "/mateu/v1/uis/com.wefox.dmtwebapp.ui.DMTUi",
                        UI.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
        return ui;
    }

}
