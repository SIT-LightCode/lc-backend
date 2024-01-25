package com.senior.dreamteam.services;

import com.senior.dreamteam.controllers.payload.HealthCheckResponse;
import com.senior.dreamteam.controllers.payload.StatusResponse;
import com.senior.dreamteam.controllers.payload.utils.Status;
import com.senior.dreamteam.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckService {
    final TagRepository tagRepository;

    public HealthCheckResponse performHealthCheck() {
        List<StatusResponse> statusResponses = new ArrayList<>();

        // Check LightCode DB
        StatusResponse lightCodeStatus = checkDBConnection();
        statusResponses.add(lightCodeStatus);

        // Calculate overall status
        Status overallStatus = calculateOverallStatus(statusResponses);

        return new HealthCheckResponse("LightCode-Backend", statusResponses, overallStatus);
    }
    public StatusResponse checkDBConnection() {
        try {
            tagRepository.executeNativeQuery();
            return new StatusResponse("LightCode DB", Status.Alive);
        } catch (Exception e) {
            log.error("Error checking LightCode DB connection", e);
            return new StatusResponse("LightCode DB", Status.Dead);
        }
    }

    private Status calculateOverallStatus(List<StatusResponse> statusResponses) {
        boolean isAllAlive = statusResponses.stream().allMatch(response -> response.status() == Status.Alive);
        return isAllAlive ? Status.Alive : Status.Dead;
    }
}
