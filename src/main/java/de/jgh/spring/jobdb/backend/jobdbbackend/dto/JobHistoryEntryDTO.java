package de.jgh.spring.jobdb.backend.jobdbbackend.dto;

import de.jgh.spring.jobdb.backend.jobdbbackend.model.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobHistoryEntryDTO {
    private LocalDateTime createDateTime, updateDateTime;
    private long durationInSeconds;
    private JobStatus jobStatus;
    private int cntImported;
}
