package de.jgh.spring.jobdb.backend.jobdbbackend.service;

import de.jgh.spring.jobdb.backend.jobdbbackend.model.Job;
import de.jgh.spring.jobdb.backend.jobdbbackend.model.JobDefinition;
import de.jgh.spring.jobdb.backend.jobdbbackend.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public List<LocalDateTime> getScheduledJobs(String jobType, LocalDateTime to) {
        List<LocalDateTime> scheduledJobs = new ArrayList<>();
        Optional<Job> lastExec = jobRepository.findFirstByJobDefinitionJobTypeEqualsOrderByCreateDateTimeDesc(jobType);
        if (lastExec.isPresent()) {
            Job lastJobExecution = lastExec.get();
            JobDefinition jobDefinition = lastJobExecution.getJobDefinition();
            String cronExpression = jobDefinition.getCronExpression();
            CronExpression cronTrigger = CronExpression.parse(cronExpression);

            LocalDateTime next = cronTrigger.next(lastJobExecution.getUpdateDateTime());
            do {
                if (next.compareTo(LocalDateTime.now()) > -1) {
                    scheduledJobs.add(next);
                }
                next = cronTrigger.next(next);
            } while (next.compareTo(to) < 1);
        }
        return scheduledJobs;
    }

}
