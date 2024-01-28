package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.payload.JwtResponse;
import com.senior.dreamteam.authentication.payload.LoginRequest;
import com.senior.dreamteam.controllers.payload.StatusResponse;
import com.senior.dreamteam.services.HealthCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class HealthCheckController {
    final HealthCheckService healthCheckService;

    @GetMapping("/details")
    public ResponseEntity<Object> healthCheck() {
        return ResponseEntity.ok(healthCheckService.performHealthCheck());
    }
}
