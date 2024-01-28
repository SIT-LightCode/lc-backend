package com.senior.dreamteam.controllers.payload;

import com.senior.dreamteam.controllers.payload.utils.Status;

import java.util.List;

public record HealthCheckResponse(String appName, List<StatusResponse> services, Status status) {
}
