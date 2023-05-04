package com.example.demo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class DataRepo {

    private long totalRqs;
    private long totalTime;

    private long rqsUnder1s;
    private long rqsOver1s;
    private long rqsOver2s;
    private long rqsOver3s;
    private long rqsOver4s;
    private long rqsOver5s;
    private long rqsOver6s;
    private long rqsOver7s;
    private long rqsOver8s;
    private long rqsOver9s;
    private long rqsOver10s;

    public void register(long time) {
        log.info("registering time " + time);
        totalRqs++;
        totalTime += time;

        if (time > 10000) {
            rqsOver10s++;
        } else if (time > 9000) {
            rqsOver9s++;
        } else if (time > 8000) {
            rqsOver8s++;
        } else if (time > 7000) {
            rqsOver7s++;
        } else if (time > 6000) {
            rqsOver6s++;
        } else if (time > 5000) {
            rqsOver5s++;
        } else if (time > 4000) {
            rqsOver4s++;
        } else if (time > 3000) {
            rqsOver3s++;
        } else if (time > 2000) {
            rqsOver2s++;
        } else if (time > 1000) {
            rqsOver1s++;
        } else {
            rqsUnder1s++;
        }
    }

    public void dump() {
        log.info("total rqs: " + totalRqs);
        log.info("total time: " + toSeconds(totalTime));
        log.info("avg time: " + toSeconds(totalTime / totalRqs));
        log.info("rqs under 1s: " + rqsUnder1s + " (" + round(100d * rqsUnder1s / totalRqs) + "%)");
        log.info("rqs over 1s: " + rqsOver1s + " (" + round(100d * rqsOver1s / totalRqs) + "%)");
        log.info("rqs over 2s: " + rqsOver2s + " (" + round(100d * rqsOver2s / totalRqs) + "%)");
        log.info("rqs over 3s: " + rqsOver3s + " (" + round(100d * rqsOver3s / totalRqs) + "%)");
        log.info("rqs over 4s: " + rqsOver4s + " (" + round(100d * rqsOver4s / totalRqs) + "%)");
        log.info("rqs over 5s: " + rqsOver5s + " (" + round(100d * rqsOver5s / totalRqs) + "%)");
        log.info("rqs over 6s: " + rqsOver6s + " (" + round(100d * rqsOver6s / totalRqs) + "%)");
        log.info("rqs over 7s: " + rqsOver7s + " (" + round(100d * rqsOver7s / totalRqs) + "%)");
        log.info("rqs over 8s: " + rqsOver8s + " (" + round(100d * rqsOver8s / totalRqs) + "%)");
        log.info("rqs over 9s: " + rqsOver9s + " (" + round(100d * rqsOver9s / totalRqs) + "%)");
        log.info("rqs over 10s: " + rqsOver10s + " (" + round(100d * rqsOver10s / totalRqs) + "%)");
    }

    private String toSeconds(long totalTime) {
        return "" + (Math.round(100d * totalTime / 1000d) / 100d) + "s";
    }

    private String round(double number) {
        return "" + (Math.round(100d * number) / 100d) + "";
    }
}
