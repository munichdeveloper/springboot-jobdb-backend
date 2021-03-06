package de.jgh.spring.jobdb.backend.jobdbbackend.service;

import de.jgh.spring.jobdb.backend.jobdbbackend.dto.JobHistoryEntryDTO;
import de.jgh.spring.jobdb.backend.jobdbbackend.model.Job;
import de.jgh.spring.jobdb.backend.jobdbbackend.model.JobDefinition;
import de.jgh.spring.jobdb.backend.jobdbbackend.repository.JobRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<JobHistoryEntryDTO> getJobHistory(String jobType, LocalDateTime from, LocalDateTime to) {
        return jobRepository.findByJobDefinitionJobTypeEqualsAndCreateDateTimeBetweenOrderByCreateDateTimeDesc(jobType, from, to)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private JobHistoryEntryDTO map(Job job) {
        JobHistoryEntryDTO jobHistoryEntryDTO = modelMapper.map(job, JobHistoryEntryDTO.class);
        LocalDateTime createDateTime = jobHistoryEntryDTO.getCreateDateTime();
        LocalDateTime updateDateTime = jobHistoryEntryDTO.getUpdateDateTime();
        long between = ChronoUnit.SECONDS.between(createDateTime, updateDateTime);
        jobHistoryEntryDTO.setDurationInSeconds(between);
        return jobHistoryEntryDTO;
    }

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
