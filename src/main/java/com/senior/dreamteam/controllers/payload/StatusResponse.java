package com.senior.dreamteam.controllers.payload;

import com.senior.dreamteam.controllers.payload.utils.Status;

public record StatusResponse(String message, Status status) { }