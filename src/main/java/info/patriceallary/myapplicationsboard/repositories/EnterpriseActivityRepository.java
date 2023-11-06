package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.EnterpriseActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseActivityRepository extends JpaRepository<EnterpriseActivity, Long> {

    EnterpriseActivity findById(long id);

}
