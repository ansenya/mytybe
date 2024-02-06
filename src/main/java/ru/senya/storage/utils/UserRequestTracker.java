package ru.senya.storage.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserRequestTracker {
    private final ConcurrentHashMap<String, Integer> requestStatusMap = new ConcurrentHashMap<>();

    public boolean isRequestInProgress(String userId) {
        if (requestStatusMap.getOrDefault(userId, 0) == 5) {
            return true;
        }
        return false;
    }

    public void setRequestInProgress(String userId, int inProgress) {
        requestStatusMap.put(userId, requestStatusMap.getOrDefault(userId, 0) + inProgress);
    }
}
