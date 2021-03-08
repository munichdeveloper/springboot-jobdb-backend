package de.jgh.spring.jobdb.backend.jobdbbackend.service;

import de.jgh.spring.jobdb.backend.jobdbbackend.dto.JobHistoryEntryDTO;
import de.jgh.spring.jobdb.backend.jobdbbackend.model.Job;
import de.jgh.spring.jobdb.backend.jobdbbackend.model.JobDefinition;
import de.jgh.spring.jobdb.backend.jobdbbackend.repository.JobDefinitionRepository;
import de.jgh.spring.jobdb.backend.jobdbbackend.repository.JobRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private JobDefinitionRepository jobDefinitionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ApplicationContext applicationContext;

    public List<JobHistoryEntryDTO> getJobHistory(String jobType, LocalDateTime from, LocalDateTime to) {
        return jobRepository.findByJobDefinitionJobTypeEqualsAndCreateDateTimeBetweenOrderByCreateDateTimeDesc(jobType, from, to)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public boolean runJob(String jobType) {
        Optional<JobDefinition> jobDefinitionOptional = jobDefinitionRepository.findByJobType(jobType);
        if (jobDefinitionOptional.isPresent()) {
            JobDefinition jobDefinition = jobDefinitionOptional.get();
            String className = jobDefinition.getClassName();
            String methodName = jobDefinition.getMethodName();
            try {
                Class<?> aClass = Class.forName(className);
                Method method = aClass.getMethod(methodName);
                Object bean = applicationContext.getBean(aClass);
                method.invoke(bean);
                return true;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
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
