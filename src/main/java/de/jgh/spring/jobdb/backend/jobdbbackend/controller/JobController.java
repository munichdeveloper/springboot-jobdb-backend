package de.jgh.spring.jobdb.backend.jobdbbackend.controller;

import de.jgh.spring.jobdb.backend.jobdbbackend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/{jobType}/{to}")
    private List<LocalDateTime> getScheduledJobs(@PathVariable String jobType, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return jobService.getScheduledJobs(jobType, to);
    }

}
