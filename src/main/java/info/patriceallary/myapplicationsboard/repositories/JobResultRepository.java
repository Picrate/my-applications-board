package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.JobResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobResultRepository extends JpaRepository<JobResult, Long> {

    JobResult findById(long id);

}
