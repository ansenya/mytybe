package ru.senya.storage.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserRequestTracker {
    private final ConcurrentHashMap<String, Integer> requestStatusMap = new ConcurrentHashMap<>();
    private static final int maxConnections = 10;

    public boolean isRequestInProgress(String userId) {
        return requestStatusMap.getOrDefault(userId, 0) == maxConnections;
    }

    public void setRequestInProgress(String userId, int inProgress) {
        requestStatusMap.put(userId, requestStatusMap.getOrDefault(userId, 0) + inProgress);
    }
}
