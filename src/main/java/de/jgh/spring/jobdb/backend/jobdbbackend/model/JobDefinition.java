package de.jgh.spring.jobdb.backend.jobdbbackend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class JobDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String cronExpression;

    private String jobType;

    private String className;
    private String methodName;

    public JobDefinition(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
