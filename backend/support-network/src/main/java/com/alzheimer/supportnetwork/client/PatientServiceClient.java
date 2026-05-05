package com.alzheimer.supportnetwork.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Real inter-service communication:
 * support-network -> patient-service (resolved through Eureka by service name).
 */
@FeignClient(name = "patient-service")
public interface PatientServiceClient {

    @GetMapping("/api/patients/{id}")
    PatientServicePatientDto getPatientById(@PathVariable("id") Long id);
}

