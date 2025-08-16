package com.example.securityapp.auth;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SessionRegistry {

    private final Map<String,SessionInfo> activeSessions = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, String ipAddress, String userAgent, int userId){
        activeSessions.put(sessionId,new SessionInfo(sessionId,ipAddress,userAgent,new Date(),userId));
    }

    public void updateLastActivity(String sessionId)
    {
        SessionInfo session = activeSessions.get(sessionId);
        if(session != null){
            session.setLastActivity(new Date());
        }
    }

    public List<SessionInfo> getUserSessions(int userId){
        return activeSessions.values().stream()
                .filter(s->s.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public void removeSession(String sessionId){
        activeSessions.remove(sessionId);
    }
}
