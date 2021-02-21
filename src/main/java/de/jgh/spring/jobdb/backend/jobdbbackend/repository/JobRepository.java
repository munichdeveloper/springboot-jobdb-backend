package de.jgh.spring.jobdb.backend.jobdbbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.jgh.spring.jobdb.backend.jobdbbackend.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Job findFirstByJobTypeEqualsOrderByCreateDateTimeDesc(String jobType);

}
