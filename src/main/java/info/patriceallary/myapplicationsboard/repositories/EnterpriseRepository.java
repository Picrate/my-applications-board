package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Enterprise findById(long id);

}
