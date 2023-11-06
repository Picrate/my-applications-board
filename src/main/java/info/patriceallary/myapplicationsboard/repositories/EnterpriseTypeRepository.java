package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.EnterpriseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseTypeRepository extends JpaRepository<EnterpriseType, Long> {

    EnterpriseType findById(long id);

}
