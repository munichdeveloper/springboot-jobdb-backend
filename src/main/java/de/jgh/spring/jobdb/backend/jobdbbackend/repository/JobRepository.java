package de.jgh.spring.jobdb.backend.jobdbbackend.repository;

import de.jgh.spring.jobdb.backend.jobdbbackend.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.jgh.spring.jobdb.backend.jobdbbackend.model.Job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findFirstByJobDefinitionJobTypeEqualsOrderByCreateDateTimeDesc(String jobType);

    Optional<Job> findFirstByJobDefinitionJobTypeEqualsAndJobStatusEqualsOrderByCreateDateTimeDesc(String jobDefinition_jobType, JobStatus jobStatus);

    List<Job> findByJobDefinitionJobTypeEqualsAndCreateDateTimeBetweenOrderByCreateDateTimeDesc(String jobType, LocalDateTime from, LocalDateTime to);
}
