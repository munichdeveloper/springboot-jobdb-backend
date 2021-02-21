package de.jgh.spring.jobdb.backend.jobdbbackend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import static javax.persistence.EnumType.STRING;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    private String jobType;

    @Enumerated(STRING)
    private JobStatus jobStatus = JobStatus.STARTED;

    private int cntImported;

    @ElementCollection
    private Set<Long> identifiers = new HashSet<>();

    public Job(String jobType) {
        this.jobType = jobType;
    }
}
