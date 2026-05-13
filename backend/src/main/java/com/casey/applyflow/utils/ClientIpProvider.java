package com.casey.applyflow.utils;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIpProvider {

    public ClientIpProvider() {};

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public String getClientIp(HttpServletRequest request) {
        return clientIp(request);
    }
}
