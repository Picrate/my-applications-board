package info.patriceallary.myapplicationsboard.controllers;

import info.patriceallary.myapplicationsboard.domain.Job;
import info.patriceallary.myapplicationsboard.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Job> findById(@PathVariable Long requestedId) {

        if(this.jobRepository.findById(requestedId).isPresent())
        {
            return ResponseEntity.ok(this.jobRepository.findById(requestedId).get());
        }
        return ResponseEntity.notFound().build();

    }

    @PostMapping()
    public ResponseEntity<Job> createANewJob(@RequestBody Job newJobRequest, UriComponentsBuilder ucb) {

        Job savedJob = new Job( newJobRequest.getName(), newJobRequest.getDateCreated(), newJobRequest.getJobResult(), newJobRequest.getEnterprise());
        this.jobRepository.save(savedJob);
        URI locationOfNewApply = ucb
                .path("/jobs/{id}")
                .buildAndExpand(savedJob.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewApply).build();
    }

    @GetMapping()
    public ResponseEntity<List<Job>> getAllJobs(Pageable pageable) {
        Page<Job> page = this.jobRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "dateCreated"))
                )
        );
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Job> updateJobs(@PathVariable Long requestedId, @RequestBody Job updatedJob) {

        if(this.jobRepository.findById(requestedId).isPresent()) {
            Job jobToUpdate = this.jobRepository.findById(requestedId).get();
            jobToUpdate.setName(updatedJob.getName());
            jobToUpdate.setDateCreated(updatedJob.getDateCreated());
            this.jobRepository.save(jobToUpdate);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deleteJob(@PathVariable Long requestedId) {
        if (this.jobRepository.findById(requestedId).isPresent()) {
            this.jobRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
