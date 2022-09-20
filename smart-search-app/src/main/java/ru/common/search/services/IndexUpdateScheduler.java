package ru.common.search.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IndexUpdateScheduler {

    private final SqlDbIndexUpdater sqlDbIndexUpdater;

    @Autowired
    public IndexUpdateScheduler(SqlDbIndexUpdater sqlDbIndexUpdater) {
        this.sqlDbIndexUpdater = sqlDbIndexUpdater;
        sqlDbIndexUpdater.updateIndex();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void trackOverduePayments() {
        System.out.println("Start index updating");
        sqlDbIndexUpdater.updateIndex();
    }
}