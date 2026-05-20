package com.casey.applyflow.interfaces;

import java.time.Duration;

public interface TokenGenerationStrategy {
    String generate();
    boolean isSecure();
    Duration getExpiration();
}
