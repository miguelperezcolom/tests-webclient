package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mateu.remote.dtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class LeadsFlowSimulator {

    @Autowired
    DataRepo dataRepo;

    ObjectMapper mapper = new ObjectMapper();

    public void simulateLeadsFlow(String baseUri, String journeyId) throws JsonProcessingException {

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

         // llamamos a la acción edit
        editLead(baseUri, journeyId, stepId);

        // preguntamos el estado del journey
        journey = getJourneyStatus(baseUri, journeyId);

        // cogemos el id del step
        stepId = journey.getCurrentStepId();

        // pedimos el step
        log.info("getting the edit step");
        step = getStep(baseUri, journeyId, stepId);

        // grabar el lead
        saveLead(baseUri, journeyId, stepId);

        // preguntamos el estado del journey
        journey = getJourneyStatus(baseUri, journeyId);

        // cogemos el id del step
        stepId = journey.getCurrentStepId();

        // pedimos el step
        step = getStep(baseUri, journeyId, stepId);

    }

    private void saveLead(String baseUri, String journeyId, String stepId) throws JsonProcessingException {
        String dataJson = "{\"data\":{\"ruiSection\":\"B\",\"legalPostCode\":null,\"operatingProvince\":\"Do not know where to get this from\",\"legalRepresentativeName\":\"admin 2 representative\",\"companyName\":\"Company 1234\",\"operatingPostCode\":\"46521\",\"descriptionDu\":\"Please upload the addtional for the related intermediary. The documents may provide information abput intermediary background, licenses, training hours, etc. Please keep in mind that up to five files can be uploaded (png, jpg or pdf) \",\"legalNumber\":null,\"title\":\"\",\"pec\":\"We do not know where to get this from\",\"operatingCity\":\"Berlin\",\"uploadDocuments\":null,\"requiredFields\":\"*Pflichtfelder\",\"entityName\":\"Lead edit view\",\"id\":\"0002e38b-53be-43f1-b5a7-3be39c272e0c\",\"legalProvince\":\"Do not know where to get this from\",\"email\":\"genesis.ortega+InterApplication@wefox.com\",\"adminEmail\":null,\"ruiNumber\":\"B154821892\",\"landlinePhoneNumber\":null,\"mobilePhoneNumber\":\"+49 6455454464\",\"new\":false,\"editor\":null,\"website\":null,\"question\":\"Is the address of the Registered Office the same as the legal quarter?\",\"legalRepresentativeSurname\":\"admin 2 \",\"legalCity\":null,\"requiredFields1\":\"*Pflichtfelder\",\"theIntermediaryHas3\":false,\"theIntermediaryHas2\":false,\"descriptionLhq\":\"Heres comes a description\",\"operatingStreet\":\"string\",\"operatingNumber\":\"21(B)\",\"answer\":true,\"legalStreet\":null,\"descriptionOhq\":\"Here's comes a description\",\"theIntermediaryHas1\":false,\"descriptionRq\":\"Please provide answers for the following questions\"}}";
        log.info("call the edit action");
        long t0 = System.currentTimeMillis();
        try {
            String rs = new RestTemplate()
                    .postForObject(baseUri + "/mateu/v1/journeys/leads/"
                                    + journeyId+ "/steps/" + stepId
                                    + "/component-0___save"
                            , RunActionRq.builder()
                                    .data(mapper.readValue(dataJson, Map.class))
                                    .build()
                            , String.class);
        } catch (HttpServerErrorException.InternalServerError error) {
            log.info("error when saving the lead as expected: " + error.getMessage());
        }
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
    }

    private void editLead(String baseUri, String journeyId, String stepId) throws JsonProcessingException {
        String dataJson = "{\"data\":{\"ruiSection\":\"B\",\"legalPostCode\":null,\"operatingProvince\":\"Do not know where to get this from\",\"companyName\":\"Company 1234\",\"legalRepresentativeName\":\"admin 2 representative\",\"operatingPostCode\":\"46521\",\"legalNumber\":null,\"title\":\"Lead - admin 2 representative admin 2 \",\"pec\":\"We do not know where to get this from\",\"operatingCity\":\"Berlin\",\"documentUpload\":\"Here we should have some checkboxes. Not clear how to manage this.\",\"entityName\":\"Lead detail view\",\"id\":\"0002e38b-53be-43f1-b5a7-3be39c272e0c\",\"legalProvince\":\"Do not know where to get this from\",\"email\":\"genesis.ortega+InterApplication@wefox.com\",\"ruiNumber\":\"B154821892\",\"landlinePhoneNumber\":null,\"mobilePhoneNumber\":\"+49 6455454464\",\"signedUpToDate\":null,\"legalRepresentativeSurname\":\"admin 2 \",\"legalCity\":null,\"operatingStreet\":\"string\",\"checkboxes\":\"Here we should have some checkboxes. Not clear how to manage this.\",\"operatingNumber\":\"21(B)\",\"legalStreet\":null,\"status\":{\"type\":\"SUCCESS\",\"message\":\"APPROVED\"}}}";
        log.info("call the edit action");
        long t0 = System.currentTimeMillis();
        String rs = new RestTemplate()
                .postForObject(baseUri + "/mateu/v1/journeys/leads/"
                                + journeyId + "/steps/" + stepId
                                + "/component-0___edit"
                        , RunActionRq.builder()
                                .data(mapper.readValue(dataJson, Map.class))
                                .build()
                        , String.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
    }

    private void openLeadDetails(String baseUri, String journeyId, String stepId, Map<String, Object> row) {
        log.info("call the edit action");
        long t0 = System.currentTimeMillis();
        String rs = new RestTemplate()
                .postForObject(baseUri + "/mateu/v1/journeys/leads/"
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
                .getForObject(baseUri + "/mateu/v1/journeys/leads/"
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
                .getForObject(baseUri + "/mateu/v1/journeys/leads/"
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
                .getForObject(baseUri + "/mateu/v1/journeys/leads/"
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
                .getForObject(baseUri + "/mateu/v1/journeys/leads/" + journeyId
                        , Journey.class);
        log.info("so far we spent " + (System.currentTimeMillis() - t0) + "ms");
        dataRepo.register(System.currentTimeMillis() - t0);
        return journey;
    }

    private void createJourney(String baseUri, String journeyId) {
        log.info("creating journey");
        long t0 = System.currentTimeMillis();
        new RestTemplate()
                .postForObject(baseUri + "/mateu/v1/journeys/leads/" + journeyId
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
