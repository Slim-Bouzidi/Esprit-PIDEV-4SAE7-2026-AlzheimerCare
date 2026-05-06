package com.alzheimer.supportnetwork.controller;

import com.alzheimer.supportnetwork.service.MissionEmailActionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Public GET endpoints for one-click mission actions from transactional email links (opaque token only).
 */
@RestController
@RequestMapping("/api/missions/email-action")
@CrossOrigin(origins = "*")
public class MissionEmailActionController {

    private final MissionEmailActionService missionEmailActionService;
    private final String emailActionRedirectBaseUrl;

    public MissionEmailActionController(
            MissionEmailActionService missionEmailActionService,
            @Value("${support.network.mission-email-result-redirect-url:http://localhost:4200/soignant-dashboard/network/missions}")
                    String emailActionRedirectBaseUrl) {
        this.missionEmailActionService = missionEmailActionService;
        this.emailActionRedirectBaseUrl = emailActionRedirectBaseUrl;
    }

    @GetMapping("/accept")
    public ResponseEntity<String> acceptFromEmail(@RequestParam(value = "token", required = false) String token) {
        MissionEmailActionService.Outcome outcome = missionEmailActionService.acceptMissionFromEmailToken(token);
        return redirectToFront("accept", outcome);
    }

    @GetMapping("/decline")
    public ResponseEntity<String> declineFromEmail(@RequestParam(value = "token", required = false) String token) {
        MissionEmailActionService.Outcome outcome = missionEmailActionService.declineMissionFromEmailToken(token);
        return redirectToFront("decline", outcome);
    }

    private ResponseEntity<String> redirectToFront(String action, MissionEmailActionService.Outcome outcome) {
        String result = mapOutcome(outcome);
        URI location =
                UriComponentsBuilder.fromUriString(emailActionRedirectBaseUrl)
                        .queryParam("emailAction", action)
                        .queryParam("result", result)
                        .queryParam("backendOutcome", outcome.name())
                        .build(true)
                        .toUri();
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location.toString()).build();
    }

    private static String mapOutcome(MissionEmailActionService.Outcome outcome) {
        return switch (outcome) {
            case OK -> "success";
            case EXPIRED -> "expired";
            case ALREADY_USED -> "already_used";
            case NOT_PENDING -> "not_pending";
            case WRONG_ASSIGNEE, WRONG_ACTION_TYPE -> "invalid";
            case MISSING_TOKEN, UNKNOWN_TOKEN, MISSION_NOT_FOUND -> "invalid_or_missing";
        };
    }
}
