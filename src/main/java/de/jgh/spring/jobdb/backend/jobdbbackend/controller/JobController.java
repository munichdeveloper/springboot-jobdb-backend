package de.jgh.spring.jobdb.backend.jobdbbackend.controller;

import de.jgh.spring.jobdb.backend.jobdbbackend.dto.JobHistoryEntryDTO;
import de.jgh.spring.jobdb.backend.jobdbbackend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/schedule/{jobType}/{to}")
    public List<LocalDateTime> getScheduledJobs(@PathVariable String jobType, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return jobService.getScheduledJobs(jobType, to);
    }

    @GetMapping("/history/{jobType}/{from}/{to}")
    public List<JobHistoryEntryDTO> getJobHistory(@PathVariable String jobType,
                                                   @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                   @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return jobService.getJobHistory(jobType, from, to);
    }

    @PostMapping("/{jobType}")
    public ResponseEntity runJob(@PathVariable String jobType) {
        if (jobService.runJob(jobType)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
