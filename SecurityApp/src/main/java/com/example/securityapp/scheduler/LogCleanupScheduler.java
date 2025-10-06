package com.example.securityapp.scheduler;

import com.example.securityapp.service.CustomLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LogCleanupScheduler {

    @Autowired
    private CustomLoggerService loggerService;

  // svaki dan u 2
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduleLogCleanup() {
        int retentionDays = 180; // 6 meseci
        System.out.println("Starting scheduled log cleanup...");
        loggerService.cleanupOldLogs(retentionDays);
        System.out.println("Log cleanup completed.");
    }
}