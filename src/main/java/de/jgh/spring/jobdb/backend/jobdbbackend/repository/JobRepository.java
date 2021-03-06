package de.jgh.spring.jobdb.backend.jobdbbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.jgh.spring.jobdb.backend.jobdbbackend.model.Job;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findFirstByJobDefinitionJobTypeEqualsOrderByCreateDateTimeDesc(String jobType);

}
