package ru.senya.storage.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserRequestTracker {
    private final ConcurrentHashMap<String, Boolean> requestStatusMap = new ConcurrentHashMap<>();

    public boolean isRequestInProgress(String userId) {
        return requestStatusMap.getOrDefault(userId, false);
    }

    public void setRequestInProgress(String userId, boolean inProgress) {
        requestStatusMap.put(userId, inProgress);
    }
}
