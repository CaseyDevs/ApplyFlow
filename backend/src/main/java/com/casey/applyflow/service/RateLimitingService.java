package com.casey.applyflow.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.casey.applyflow.exception.RateLimitExceededException;
import com.casey.applyflow.utils.ClientIpProvider;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class RateLimitingService {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ClientIpProvider clientIpProvider;
    
    public RateLimitingService(ClientIpProvider clientIpProvider) {
        this.clientIpProvider = clientIpProvider;
    }

    public void checkRateLimit(HttpServletRequest httpRequest, String bucketKey, int capacity, int refillCount, int refillMinutes) {
        String clientIp = clientIpProvider.getClientIp(httpRequest);
        String key = bucketKey + ":" + clientIp;

        Bucket bucket = buckets.computeIfAbsent(key, val -> {
            Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(refillCount, Duration.ofMinutes(refillMinutes))
                .build();
            return Bucket.builder().addLimit(limit).build(); 
        });

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }
    }
}
