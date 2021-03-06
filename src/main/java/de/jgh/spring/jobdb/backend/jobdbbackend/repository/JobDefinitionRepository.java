package de.jgh.spring.jobdb.backend.jobdbbackend.repository;

import de.jgh.spring.jobdb.backend.jobdbbackend.model.JobDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobDefinitionRepository extends JpaRepository<JobDefinition, Long> {
    Optional<JobDefinition> findByJobType(String jobType);
}
