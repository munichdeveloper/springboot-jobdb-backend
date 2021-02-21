package de.jgh.spring.jobdb.backend.jobdbbackend.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static de.jgh.spring.jobdb.backend.jobdbbackend.model.JobStatus.ERROR;
import static de.jgh.spring.jobdb.backend.jobdbbackend.model.JobStatus.FINISHED;

import de.jgh.spring.jobdb.backend.jobdbbackend.dto.ScheduledJobResult;
import de.jgh.spring.jobdb.backend.jobdbbackend.model.Job;
import de.jgh.spring.jobdb.backend.jobdbbackend.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Configuration
@Slf4j
public class ScheduledAspect {

	@Autowired
	private JobRepository jobRepository;

	@Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
	public Object logJobexecution(ProceedingJoinPoint joinPoint) throws Throwable {
		ScheduledJobResult scheduledJobResult = (ScheduledJobResult) joinPoint.proceed();

		if (scheduledJobResult != null) {
			Job job = jobRepository.save(new Job());

			try {
				job.setJobStatus(FINISHED);
				job.setCntImported(scheduledJobResult.getImportCnt());
				job.setJobType(scheduledJobResult.getJobType());
				job.setIdentifiers(scheduledJobResult.getIdentifiers());
			} catch (Exception e) {
				job.setJobStatus(ERROR);
				log.error(e.getMessage());
			} finally {
				jobRepository.save(job);
			}
		}

		return scheduledJobResult;
	}

}
