package de.jgh.spring.jobdb.backend.jobdbbackend.aspect;

import de.jgh.spring.jobdb.backend.jobdbbackend.dto.ScheduledJobResult;
import de.jgh.spring.jobdb.backend.jobdbbackend.model.Job;
import de.jgh.spring.jobdb.backend.jobdbbackend.model.JobDefinition;
import de.jgh.spring.jobdb.backend.jobdbbackend.repository.JobDefinitionRepository;
import de.jgh.spring.jobdb.backend.jobdbbackend.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static de.jgh.spring.jobdb.backend.jobdbbackend.model.JobStatus.ERROR;
import static de.jgh.spring.jobdb.backend.jobdbbackend.model.JobStatus.FINISHED;

@Aspect
@Configuration
@Slf4j
public class ScheduledAspect {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobDefinitionRepository jobDefinitionRepository;

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object logJobexecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Scheduled annotation = method.getAnnotation(Scheduled.class);
        String cronExpression = annotation.cron();
        String jobType = StringUtils.capitalize(method.getName());

        Job job = jobRepository.save(new Job());

        ScheduledJobResult scheduledJobResult = null;
        try {
            scheduledJobResult = (ScheduledJobResult) joinPoint.proceed();
            if (scheduledJobResult != null) {
                job.setJobStatus(FINISHED);
                job.setCntImported(scheduledJobResult.getImportCnt());
                job.setIdentifiers(scheduledJobResult.getIdentifiers());
            }
        } catch (Exception e) {
            job.setJobStatus(ERROR);
            log.error("error occured in job execution: " + jobType, e);
        } finally {
            if (scheduledJobResult != null) {
                JobDefinition jobDefinition = jobDefinitionRepository.findByJobType(jobType).orElseGet(() -> new JobDefinition(cronExpression));
                jobDefinition.setJobType(jobType);
                jobDefinition.setCronExpression(cronExpression);
                jobDefinitionRepository.save(jobDefinition);
                job.setJobDefinition(jobDefinition);
                jobRepository.save(job);
            } else {
                jobRepository.delete(job);
            }
        }

        return scheduledJobResult;
    }

}
